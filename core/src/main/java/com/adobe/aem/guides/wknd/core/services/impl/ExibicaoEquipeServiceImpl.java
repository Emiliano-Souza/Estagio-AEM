package com.adobe.aem.guides.wknd.core.services.impl;

import com.adobe.aem.guides.wknd.core.config.ExibicaoEquipeConfig;
import com.adobe.aem.guides.wknd.core.services.ExibicaoEquipeService;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.Designate;

@Component(service = ExibicaoEquipeService.class)
@Designate(ocd = ExibicaoEquipeConfig.class)
public class ExibicaoEquipeServiceImpl implements ExibicaoEquipeService {

    private volatile int maxMembros;

    @Activate
    @Modified
    protected void activate(ExibicaoEquipeConfig config) {
        this.maxMembros = config.maxMembros();
    }

    @Override
    public int getMaxMembros() {
        return maxMembros;
    }
}