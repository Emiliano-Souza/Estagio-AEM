# Desafio 7.1 — Componente Equipe WKND

## Resumo

Implementação de um componente AEM para cadastro e exibição de membros de uma equipe no projeto WKND.

O componente permite cadastrar vários integrantes por meio de um **Multifield composto**, contendo:

- Nome
- Cargo
- Foto

Os dados são lidos por Sling Models e renderizados no HTL. A quantidade máxima de membros exibidos é controlada por um serviço OSGi configurável, permitindo alteração em tempo de execução sem necessidade de novo deploy.

---

## Funcionalidades implementadas

- Componente `Equipe WKND`
- Multifield composto
- Cadastro de vários membros
- Campos de nome, cargo e foto
- Persistência dos membros como recursos filhos no JCR
- Leitura da coleção com `@ChildResource`
- Renderização com `data-sly-list`
- Serviço OSGi configurável
- Limite de membros definido por `maxMembros`
- Injeção do serviço com `@OSGiService`
- Atualização da configuração sem redeploy
- Injeções opcionais
- Valores padrão para campos ausentes
- Tratamento de lista nula ou vazia
- Degradação elegante após remoção de propriedades no CRXDE

---

## Estrutura da implementação

### Java

```text
core/src/main/java/com/adobe/aem/guides/wknd/core/
├── config/
│   └── ExibicaoEquipeConfig.java
├── models/
│   ├── EquipeModel.java
│   └── MembroEquipeModel.java
└── services/
    ├── ExibicaoEquipeService.java
    └── impl/
        └── ExibicaoEquipeServiceImpl.java
```

### Componente AEM

```text
ui.apps/src/main/content/jcr_root/apps/wknd/components/equipe/
├── .content.xml
├── _cq_dialog/
│   └── .content.xml
├── _cq_design_dialog/
│   └── .content.xml
├── clientlibs/
│   ├── .content.xml
│   ├── css.txt
│   └── css/
│       └── equipe.css
└── equipe.html
```

---

## Multifield composto

O dialog do componente utiliza um Multifield composto para permitir o cadastro de vários membros.

Cada item do Multifield possui os campos:

```text
nome
cargo
foto
```

No repositório JCR, os dados são armazenados como recursos filhos:

```text
equipe
└── membros
    ├── item0
    │   ├── nome
    │   ├── cargo
    │   └── foto
    ├── item1
    │   ├── nome
    │   ├── cargo
    │   └── foto
    └── item2
        ├── nome
        ├── cargo
        └── foto
```

Como os membros são armazenados como nós filhos, eles não podem ser tratados apenas com `@ValueMapValue`.

A coleção é injetada no model principal utilizando:

```java
@ChildResource(name = "membros")
private List<MembroEquipeModel> membros;
```

---

## Sling Models

### EquipeModel

O `EquipeModel` representa o componente principal.

Suas responsabilidades são:

- Ler as propriedades do componente
- Injetar a lista de membros
- Injetar o serviço OSGi
- Consultar o limite configurado
- Limitar a quantidade de membros exibidos
- Retornar uma lista segura para o HTL
- Disponibilizar configurações visuais da policy

O model principal é adaptado a partir de:

```java
SlingHttpServletRequest
```

Essa escolha foi utilizada porque o componente é executado no contexto de uma requisição e também pode acessar objetos relacionados à renderização atual.

---

### MembroEquipeModel

O `MembroEquipeModel` representa cada item cadastrado no Multifield.

Ele é adaptado a partir de:

```java
Resource
```

Essa escolha foi utilizada porque cada membro é armazenado como um recurso filho dentro do nó `membros`.

O model contém os campos:

```text
nome
cargo
foto
```

---

## Estratégia de injeção

Foi utilizada a estratégia:

```java
defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
```

Essa configuração permite que o Sling Model seja criado mesmo quando algum dado não estiver presente.

Isso evita falhas em situações como:

- Componente recém-adicionado
- Campo opcional não preenchido
- Conteúdo antigo
- Conteúdo incompleto
- Propriedade removida manualmente
- Ausência do nó `membros`
- Serviço temporariamente indisponível

Sem a estratégia opcional, a ausência de uma propriedade poderia impedir a adaptação completa do Sling Model.

---

## Uso de @Default

Nos campos textuais foi utilizado:

```java
@Default(values = "")
```

Quando uma propriedade não existe, o campo recebe uma string vazia.

Exemplo:

```java
@ValueMapValue
@Default(values = "")
private String cargo;
```

Essa decisão evita valores nulos no HTL e permite que o componente continue sendo renderizado mesmo quando uma informação estiver ausente.

---

## Tratamento da lista de membros

O `@ChildResource` pode retornar uma lista nula quando o nó `membros` não existe.

Por isso, o model trata esse cenário antes de aplicar qualquer operação sobre a coleção.

Exemplo da estratégia utilizada:

```java
if (membros == null || membros.isEmpty()) {
    return Collections.emptyList();
}
```

Dessa forma, o HTL recebe uma lista vazia em vez de um valor nulo.

Isso evita erros como:

```text
NullPointerException
```

O componente continua funcionando mesmo quando nenhum membro está cadastrado ou quando o nó completo é removido no CRXDE.

---

## Serviço OSGi

A regra de limite de membros foi separada em um serviço OSGi.

Arquivos relacionados:

```text
ExibicaoEquipeConfig.java
ExibicaoEquipeService.java
ExibicaoEquipeServiceImpl.java
```

O serviço disponibiliza o valor de:

```text
maxMembros
```

Esse valor define a quantidade máxima de integrantes que o componente pode exibir.

---

## Motivo para utilizar um serviço OSGi

A regra de limite foi colocada em um serviço OSGi para não ficar fixa dentro do Sling Model.

Essa separação permite:

- Alterar o limite sem modificar o código
- Alterar o limite sem recompilar o projeto
- Alterar o limite sem realizar novo deploy
- Centralizar a regra de exibição
- Reutilizar a configuração em outros componentes
- Separar regra de negócio e apresentação

---

## Configuração OSGi

A configuração foi declarada com:

```java
@ObjectClassDefinition
```

A implementação do serviço foi associada à configuração com:

```java
@Designate
```

O valor configurado é carregado pelos métodos:

```java
@Activate
@Modified
```

O uso de `@Modified` permite que o serviço seja atualizado quando a configuração é alterada no Console OSGi.

---

## Alteração sem redeploy

O valor de `maxMembros` pode ser alterado no Console OSGi:

```text
http://localhost:4502/system/console/configMgr
```

Configuração utilizada:

```text
ExibicaoEquipeService
```

Após salvar o novo valor:

1. O OSGi atualiza a configuração
2. O método marcado com `@Modified` é executado
3. O novo limite é armazenado pelo serviço
4. O Sling Model consulta o valor atualizado
5. A página passa a exibir a nova quantidade

Nenhum novo build ou deploy é necessário.

---

## Injeção do serviço no Sling Model

O serviço é recebido pelo `EquipeModel` por meio de:

```java
@OSGiService
private ExibicaoEquipeService exibicaoEquipeService;
```

O model consulta o serviço para determinar quantos membros devem ser retornados ao HTL.

A regra de limitação permanece no backend e não é executada diretamente no arquivo HTML.

---

## Renderização no HTL

O arquivo `equipe.html` utiliza `data-sly-list` para percorrer os membros retornados pelo model.

Exemplo simplificado:

```html
<div data-sly-list.membro="${model.membros}">
    <article class="cmp-equipe__card">
        <img
            src="${membro.foto}"
            alt="${membro.nome}"
            class="cmp-equipe__foto">

        <h3 class="cmp-equipe__nome">
            ${membro.nome}
        </h3>

        <p class="cmp-equipe__cargo">
            ${membro.cargo}
        </p>
    </article>
</div>
```

O HTL ficou responsável apenas pela apresentação.

As responsabilidades foram separadas da seguinte forma:

```text
Dialog          → cadastro dos membros
JCR             → persistência dos dados
Sling Models    → leitura e preparação dos dados
Serviço OSGi    → regra de limite
HTL             → renderização
Clientlib       → apresentação visual
```

---

## Policy e configurações visuais

As opções visuais foram separadas do conteúdo editorial utilizando a Design Dialog e a policy do template.

Essa decisão permite que o autor edite os membros pelo dialog, enquanto as configurações visuais permanecem controladas pela policy.

Assim, o componente separa:

```text
Conteúdo editorial → dialog
Configuração visual → policy
Regra de limite    → OSGi
```

---

## Degradação elegante

Foram realizados testes removendo dados diretamente no CRXDE.

### Teste 1 — Remoção do campo cargo

Uma propriedade `cargo` foi removida de um membro.

Resultado:

- O componente continuou funcionando
- O membro continuou sendo exibido
- O campo ausente recebeu valor padrão
- Nenhuma exceção foi apresentada

### Teste 2 — Remoção de um membro

Um nó de membro foi removido do JCR.

Resultado:

- Os membros restantes continuaram sendo exibidos
- A lista foi atualizada normalmente
- O componente não apresentou erro

### Teste 3 — Remoção do nó membros

O nó completo `membros` foi removido.

Resultado:

- O model retornou uma lista vazia
- O HTL não tentou percorrer um valor nulo
- O componente continuou sendo renderizado
- Nenhum `NullPointerException` ocorreu

---

## Decisões técnicas

### Uso de @ChildResource

Foi utilizado porque os itens do Multifield são armazenados como recursos filhos no JCR.

### Model de membro adaptado de Resource

Cada membro corresponde diretamente a um recurso filho.

### Model principal adaptado de SlingHttpServletRequest

O componente principal é executado no contexto da requisição e pode acessar elementos relacionados à renderização.

### Uso de DefaultInjectionStrategy.OPTIONAL

Evita que a ausência de uma propriedade impeça a criação do model.

### Uso de @Default

Define valores seguros para campos textuais ausentes.

### Retorno de lista vazia

Evita o envio de valores nulos ao HTL.

### Uso de serviço OSGi

Mantém a regra de limite fora do componente e permite alteração em tempo de execução.

### Uso de @Activate e @Modified

Permite carregar a configuração inicialmente e atualizar o serviço após mudanças no Console OSGi.

### Regra de limite no backend

Evita colocar regra de negócio no HTL e mantém a apresentação mais simples.

---

## Evidências

### Dialog com Multifield

![Dialog Multifield](./7.1-dialog-multifield.png)

O dialog permite cadastrar vários membros, cada um com nome, cargo e foto.

---

### Equipe renderizada

![Equipe renderizada](./7.1-equipe-renderizada.png)

O componente renderiza os integrantes cadastrados no Multifield.

---

### Configuração OSGi antes da alteração

![OSGi antes](./7.1-osgi-antes.png)

Registro do valor inicial configurado para `maxMembros`.

---

### Configuração OSGi depois da alteração

![OSGi depois](./7.1-osgi-depois.png)

Registro da alteração do limite pelo Console OSGi.

---

### Resultado depois da alteração OSGi

![Equipe após configuração OSGi](./7.1-equipe-osg-depois.png)

A página passou a respeitar o novo limite sem necessidade de redeploy.

---

### Propriedade antes da remoção no CRXDE

![Propriedade antes da remoção](./7.1-propriedade-antes-removida-crxde.png)

Registro do membro antes da remoção da propriedade.

---

### Propriedade removida no CRXDE

![Propriedade removida](./7.1-propriedade-removida-crxde.png)

Registro da propriedade removida diretamente no repositório.

---

### Degradação elegante

![Degradação elegante](./7.1-degradacao-elegante.png)

O componente continuou funcionando mesmo após a remoção de dados.

---

## Evidências em vídeo

### Alteração do limite sem redeploy

[Assistir ao vídeo](./7.1-MaxMember-NoRedeploy.mp4)

Demonstração da alteração de `maxMembros` no Console OSGi e atualização do componente sem novo deploy.

### Remoção de campo no CRXDE

[Assistir ao vídeo](./7.1-crxe-campo-deletado.mp4)

Demonstração da remoção de uma propriedade e do comportamento resiliente do componente.

### Remoção de membro no CRXDE

[Assistir ao vídeo](./7.1-crxe-membro-deletado.mp4)

Demonstração da remoção de um membro completo sem causar falha na renderização.

---

## Validação do projeto

O projeto foi validado com:

```bash
mvn clean install -DskipTests
```

Resultado:

```text
BUILD SUCCESS
```

Também foram verificados:

- Componente disponível no editor
- Dialog funcionando
- Multifield persistindo os dados
- Sling Models ativos
- Serviço OSGi ativo
- Alteração de configuração sem redeploy
- Tratamento de propriedades ausentes
- Tratamento de lista vazia
- Renderização correta no HTL

---

## Resultado final

O componente `Equipe WKND` atende aos requisitos do Desafio 7.1.

A implementação possui:

- Multifield composto
- Sling Models
- Injeção com `@ChildResource`
- Renderização com `data-sly-list`
- Serviço OSGi configurável
- Alteração em tempo de execução
- Tratamento de valores ausentes
- Degradação elegante
- Separação entre conteúdo, apresentação e regra de negócio
- Evidências em imagens e vídeos