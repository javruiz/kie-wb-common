package org.kie.workbench.common.screens.explorer.client.widgets.navigator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.github.gwtbootstrap.client.ui.Dropdown;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.NavList;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import org.guvnor.common.services.project.model.Project;
import org.jboss.errai.ioc.client.container.IOC;
import org.kie.workbench.common.screens.explorer.client.resources.i18n.ProjectExplorerConstants;
import org.kie.workbench.common.screens.explorer.client.widgets.ViewPresenter;
import org.kie.workbench.common.screens.explorer.model.FolderListing;
import org.uberfire.backend.organizationalunit.OrganizationalUnit;
import org.uberfire.backend.repositories.Repository;
import org.uberfire.client.resources.NavigatorResources;

import static org.uberfire.commons.validation.PortablePreconditions.*;

public class Explorer extends Composite {

    public static enum Mode {
        REGULAR, EXPANDED, COLLAPSED
    }

    public static enum NavType {
        TREE, BREADCRUMB
    }

    private final FlowPanel container = new FlowPanel();
    private final Dropdown organizationUnits = new Dropdown();
    private final Dropdown repos = new Dropdown();
    private final Dropdown prjs = new Dropdown();

    private Mode mode = Mode.REGULAR;

    private boolean isAlreadyInitialized = false;

    private ViewPresenter presenter = null;
    private Navigator activeNavigator = null;
    private Map<NavType, Navigator> navigators = new HashMap<NavType, Navigator>();

    public Explorer() {
        initWidget( container );
        setStyleName( NavigatorResources.INSTANCE.css().container() );
    }

    public void init( final Mode mode,
                      final NavigatorOptions options,
                      final NavType navType,
                      final ViewPresenter presenter ) {
        this.presenter = presenter;
        this.mode = mode;
        setNavType( navType, options );
    }

    public void setNavType( final NavType navType,
                            final NavigatorOptions options ) {
        checkNotNull( "navType", navType );
        if ( activeNavigator != null ) {
            if ( navType.equals( NavType.TREE ) && activeNavigator instanceof TreeNavigator ) {
                activeNavigator.loadContent( presenter.getActiveContent() );
                return;
            } else if ( navType.equals( NavType.BREADCRUMB ) && activeNavigator instanceof BreadcrumbNavigator ) {
                activeNavigator.loadContent( presenter.getActiveContent() );
                return;
            }
            container.remove( activeNavigator );
        }

        if ( !navigators.containsKey( navType ) ) {
            if ( navType.equals( NavType.TREE ) ) {
                activeNavigator = IOC.getBeanManager().lookupBean( TreeNavigator.class ).getInstance();
            } else {
                activeNavigator = IOC.getBeanManager().lookupBean( BreadcrumbNavigator.class ).getInstance();
            }
            activeNavigator.setPresenter( presenter );
            activeNavigator.setOptions( options );
            navigators.put( navType, activeNavigator );
        } else {
            activeNavigator = navigators.get( navType );
        }

        if ( mode.equals( Mode.EXPANDED ) ) {
            container.add( activeNavigator );
        }

        activeNavigator.loadContent( presenter.getActiveContent() );
    }

    public void clear() {
        for ( final Navigator navigator : navigators.values() ) {
            navigator.clear();
        }
    }

    public void setupHeader( final Set<OrganizationalUnit> organizationalUnits,
                             final OrganizationalUnit activeOrganizationalUnit,
                             final Set<Repository> repositories,
                             final Repository activeRepository,
                             final Set<Project> projects,
                             final Project activeProject ) {

        this.organizationUnits.clear();
        if ( activeOrganizationalUnit != null ) {
            this.organizationUnits.setText( activeOrganizationalUnit.getName() );
        }
        this.organizationUnits.add( new NavList() {{
            for ( final OrganizationalUnit ou : organizationalUnits ) {
                add( new NavLink( ou.getName() ) {{
                    addClickHandler( new ClickHandler() {
                        @Override
                        public void onClick( ClickEvent event ) {
                            presenter.organizationalUnitSelected( ou );
                        }
                    } );
                }} );
            }
        }} );

        this.repos.clear();
        if ( activeRepository != null ) {
            this.repos.setText( activeRepository.getAlias() );
        }
        this.repos.add( new NavList() {{
            for ( final Repository repository : repositories ) {
                add( new NavLink( repository.getAlias() ) {{
                    addClickHandler( new ClickHandler() {
                        @Override
                        public void onClick( ClickEvent event ) {
                            presenter.repositorySelected( repository );
                        }
                    } );
                }} );
            }
        }} );

        this.prjs.clear();
        if ( activeProject != null ) {
            this.prjs.setText( activeProject.getProjectName() );
        }
        this.prjs.add( new NavList() {{
            for ( final Project project : projects ) {
                add( new NavLink( project.getProjectName() ) {{
                    addClickHandler( new ClickHandler() {
                        @Override
                        public void onClick( ClickEvent event ) {
                            presenter.projectSelected( project );
                        }
                    } );
                }} );
            }
        }} );

        if ( organizationalUnits.isEmpty() ) {
            this.organizationUnits.setText( ProjectExplorerConstants.INSTANCE.nullEntry() );
            this.organizationUnits.add( new NavLink( ProjectExplorerConstants.INSTANCE.nullEntry() ) );
            this.organizationUnits.getTriggerWidget().setEnabled( false );
            this.repos.setText( ProjectExplorerConstants.INSTANCE.nullEntry() );
            this.repos.add( new NavLink( ProjectExplorerConstants.INSTANCE.nullEntry() ) );
            this.repos.getTriggerWidget().setEnabled( false );
            this.prjs.setText( ProjectExplorerConstants.INSTANCE.nullEntry() );
            this.prjs.getTriggerWidget().setEnabled( false );
            this.prjs.add( new NavLink( ProjectExplorerConstants.INSTANCE.nullEntry() ) );
        } else if ( repositories.isEmpty() ) {
            this.repos.setText( ProjectExplorerConstants.INSTANCE.nullEntry() );
            this.repos.getTriggerWidget().setEnabled( false );
            this.repos.add( new NavLink( ProjectExplorerConstants.INSTANCE.nullEntry() ) );
            this.prjs.setText( ProjectExplorerConstants.INSTANCE.nullEntry() );
            this.prjs.getTriggerWidget().setEnabled( false );
            this.prjs.add( new NavLink( ProjectExplorerConstants.INSTANCE.nullEntry() ) );
        } else if ( projects.isEmpty() ) {
            this.prjs.setText( ProjectExplorerConstants.INSTANCE.nullEntry() );
            this.prjs.add( new NavLink( ProjectExplorerConstants.INSTANCE.nullEntry() ) );
            this.prjs.getTriggerWidget().setEnabled( false );
        }

        if ( !isAlreadyInitialized ) {
            container.clear();
            if ( !mode.equals( Mode.REGULAR ) ) {
                final Element element = DOM.createElement( "i" );
                element.getStyle().setFloat( Style.Float.RIGHT );
                element.getStyle().setPaddingTop( 5, Style.Unit.PX );
                element.getStyle().setPaddingRight( 10, Style.Unit.PX );
                DOM.sinkEvents( (com.google.gwt.user.client.Element) element, Event.ONCLICK );
                DOM.setEventListener( (com.google.gwt.user.client.Element) element, new EventListener() {
                    public void onBrowserEvent( Event event ) {
                        if ( element.getClassName().equals( "icon-expand-alt" ) ) {
                            element.setClassName( "icon-collapse-alt" );
                            onExpandNavigator();
                        } else {
                            element.setClassName( "icon-expand-alt" );
                            onCollapseNavigator();
                        }
                    }
                } );

                if ( mode.equals( Mode.COLLAPSED ) ) {
                    element.setClassName( "icon-expand-alt" );
                } else {
                    element.setClassName( "icon-collapse-alt" );
                }

                container.getElement().appendChild( element );
            }

            container.add( new NavigatorBreadcrumbs( NavigatorBreadcrumbs.Mode.HEADER ) {{
                build( organizationUnits, repos, prjs );
            }} );

            if ( activeNavigator != null && mode.equals( Mode.EXPANDED ) ) {
                container.add( activeNavigator );
            }

            isAlreadyInitialized = true;
        }
    }

    public void loadContent( final FolderListing content ) {
        if ( content != null ) {
            activeNavigator.loadContent( content );
        }
    }

    private void onCollapseNavigator() {
        if ( activeNavigator.isAttached() ) {
            container.remove( activeNavigator );
        }
        mode = Mode.COLLAPSED;
    }

    private void onExpandNavigator() {
        if ( !activeNavigator.isAttached() ) {
            container.add( activeNavigator );
        }
        mode = Mode.EXPANDED;
    }
}