/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.widgets.client.datamodel;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.workbench.models.datamodel.imports.HasImports;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.kie.workbench.common.services.datamodel.model.PackageDataModelOracleBaselinePayload;
import org.uberfire.backend.vfs.Path;

@ApplicationScoped
public class AsyncPackageDataModelOracleFactory {

    @Inject
    private SyncBeanManager iocManager;

    public AsyncPackageDataModelOracle makeAsyncPackageDataModelOracle( final Path resourcePath,
                                                                        final PackageDataModelOracleBaselinePayload payload ) {
        final AsyncPackageDataModelOracle oracle = iocManager.lookupBean( AsyncPackageDataModelOracle.class ).getInstance();
        populate( oracle,
                  payload );
        oracle.init( resourcePath );
        oracle.filter();
        return oracle;
    }

    public AsyncPackageDataModelOracle makeAsyncPackageDataModelOracle( final Path resourcePath,
                                                                        final HasImports hasImports,
                                                                        final PackageDataModelOracleBaselinePayload payload ) {
        final AsyncPackageDataModelOracle oracle = iocManager.lookupBean( AsyncPackageDataModelOracle.class ).getInstance();
        populate( oracle,
                  payload );
        oracle.init( resourcePath );
        oracle.filter( hasImports.getImports() );
        return oracle;
    }

    public void destroy( final AsyncPackageDataModelOracle oracle ) {
        iocManager.destroyBean( oracle );
    }

    private void populate( final AsyncPackageDataModelOracle oracle,
                           final PackageDataModelOracleBaselinePayload payload ) {
        oracle.setProjectName( payload.getProjectName() );
        oracle.addModelFields( payload.getModelFields() );
        oracle.addRuleNames( payload.getRuleNames() );
        oracle.addFieldParametersType( payload.getFieldParametersType() );
        oracle.addEventTypes( payload.getEventTypes() );
        oracle.addTypeSources( payload.getTypeSources() );
        oracle.addSuperTypes( payload.getSuperTypes() );
        oracle.addTypeAnnotations( payload.getTypeAnnotations() );
        oracle.addTypeFieldsAnnotations( payload.getTypeFieldsAnnotations() );
        oracle.addJavaEnumDefinitions( payload.getJavaEnumDefinitions() );
        oracle.addMethodInformation( payload.getMethodInformation() );
        oracle.addCollectionTypes( payload.getCollectionTypes() );
        oracle.addPackageNames( payload.getPackageNames() );

        oracle.setPackageName( payload.getPackageName() );
        oracle.addWorkbenchEnumDefinitions( payload.getWorkbenchEnumDefinitions() );
        oracle.addDslConditionSentences( payload.getDslConditionSentences() );
        oracle.addDslActionSentences( payload.getDslActionSentences() );
        oracle.addGlobals( payload.getGlobals() );
    }

}
