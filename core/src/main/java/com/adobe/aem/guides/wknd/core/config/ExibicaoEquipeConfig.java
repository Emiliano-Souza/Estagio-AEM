package com.adobe.aem.guides.wknd.core.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(
    name = "WKND — Exibição da equipe",
    description = "Configura as regras de exibição do componente Equipe WKND."
)
public @interface ExibicaoEquipeConfig {

    @AttributeDefinition(
        name = "Máximo de membros",
        description = "Quantidade máxima de membros exibidos pelo componente.",
        min = "1"
    )
    int maxMembros() default 4;
}