/*
 * Copyright 2012 JBoss Inc
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

package org.kie.workbench.common.screens.projecteditor.client.forms;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.DataGrid;
import com.github.gwtbootstrap.client.ui.TooltipCellDecorator;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.project.model.ClockTypeOption;
import org.guvnor.common.services.project.model.KSessionModel;
import org.kie.workbench.common.screens.datamodeller.client.widgets.ClickableImageResourceCell;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.kie.workbench.common.screens.projecteditor.client.resources.i18n.ProjectEditorConstants;
import org.kie.workbench.common.widgets.client.resources.CommonImages;

public class KSessionsPanelViewImpl
        extends Composite
        implements KSessionsPanelView {

    private Presenter presenter;

    interface Binder
            extends
            UiBinder<Widget, KSessionsPanelViewImpl> {

    }

    private static Binder uiBinder = GWT.create(Binder.class);

    @UiField(provided = true)
    DataGrid<KSessionModel> grid;

    @UiField
    Button addButton;

    private final KSessionModelOptionsPopUp kSessionModelOptionsPopUp;

    @Inject
    public KSessionsPanelViewImpl(KSessionModelOptionsPopUp kSessionModelOptionsPopUp) {
        this.kSessionModelOptionsPopUp = kSessionModelOptionsPopUp;
        grid = new DataGrid<KSessionModel>();

        grid.setEmptyTableWidget(new Label("---"));

        setUpNameColumn();
        setUpDefaultColumn();
        setUpStateColumn();
        setUpClockColumn();
        setUpOptionsColumn();
        setUpRemoveColumn();

        grid.setBordered(true);

        initWidget(uiBinder.createAndBindUi(this));
    }

    private void setUpNameColumn() {
        Column<KSessionModel, String> column = new Column<KSessionModel, String>(new EditTextCell()) {
            @Override
            public String getValue(KSessionModel kSessionModel) {
                return kSessionModel.getName();
            }
        };
        grid.addColumn(column, ProjectEditorResources.CONSTANTS.Name());

        column.setFieldUpdater(new FieldUpdater<KSessionModel, String>() {
            @Override
            public void update(int i, KSessionModel kSessionModel, String value) {
                kSessionModel.setName(value);
            }
        });
    }

    private void setUpClockColumn() {
        ArrayList<String> options = new ArrayList<String>();
        options.add(ProjectEditorResources.CONSTANTS.Realtime());
        options.add(ProjectEditorResources.CONSTANTS.Pseudo());

        Column<KSessionModel, String> column = new Column<KSessionModel, String>(new SelectionCell(options)) {
            @Override
            public String getValue(KSessionModel kSessionModel) {
                if (kSessionModel.getClockType().equals(ClockTypeOption.PSEUDO)) {
                    return ProjectEditorResources.CONSTANTS.Pseudo();
                } else if (kSessionModel.getClockType().equals(ClockTypeOption.REALTIME)) {
                    return ProjectEditorResources.CONSTANTS.Realtime();
                } else {
                    return kSessionModel.getClockType().toString();
                }
            }
        };

        column.setFieldUpdater(new FieldUpdater<KSessionModel, String>() {
            @Override
            public void update(int index, KSessionModel model, String value) {
                if (value.equals(ProjectEditorResources.CONSTANTS.Pseudo())) {
                    model.setClockType(ClockTypeOption.PSEUDO);
                } else {
                    model.setClockType(ClockTypeOption.REALTIME);
                }
            }
        });

        grid.addColumn(column, ProjectEditorResources.CONSTANTS.Clock());
    }

    private void setUpStateColumn() {
        ArrayList<String> options = new ArrayList<String>();
        options.add(ProjectEditorResources.CONSTANTS.Stateful());
        options.add(ProjectEditorResources.CONSTANTS.Stateless());

        Column<KSessionModel, String> column = new Column<KSessionModel, String>(new SelectionCell(options)) {
            @Override
            public String getValue(KSessionModel kSessionModel) {
                if (kSessionModel.getType() == null) {
                    return ProjectEditorResources.CONSTANTS.Stateful();
                } else if (kSessionModel.getType().equals("stateful")) {
                    return ProjectEditorResources.CONSTANTS.Stateful();
                } else if (kSessionModel.getType().equals("stateless")) {
                    return ProjectEditorResources.CONSTANTS.Stateless();
                } else {
                    return kSessionModel.getType();
                }
            }
        };

        column.setFieldUpdater(new FieldUpdater<KSessionModel, String>() {
            @Override
            public void update(int index, KSessionModel model, String value) {
                if (value.equals(ProjectEditorResources.CONSTANTS.Stateful())) {
                    model.setType("stateful");
                } else {
                    model.setType("stateless");
                }
            }
        });

        grid.addColumn(column, ProjectEditorResources.CONSTANTS.State());
    }

    private void setUpDefaultColumn() {
        Column<KSessionModel, Boolean> column = new Column<KSessionModel, Boolean>(new CheckboxCell()) {
            @Override public Boolean getValue(KSessionModel model) {
                return model.isDefault();
            }
        };

        column.setFieldUpdater(new FieldUpdater<KSessionModel, Boolean>() {
            @Override
            public void update(int index, KSessionModel model, Boolean value) {
                model.setDefault(value);
                presenter.onDefaultChanged(model);
            }
        });

        column.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

        grid.addColumn(column, ProjectEditorResources.CONSTANTS.Default());
        grid.setColumnWidth(column, "80px");
    }

    private void setUpOptionsColumn() {
        ClickableImageResourceCell typeImageCell = new ClickableImageResourceCell(true);
        TooltipCellDecorator<ImageResource> decorator = new TooltipCellDecorator<ImageResource>(typeImageCell);
        decorator.setText(ProjectEditorResources.CONSTANTS.Options());
        Column<KSessionModel, ImageResource> column = new Column<KSessionModel, ImageResource>(decorator) {
            @Override
            public ImageResource getValue(KSessionModel model) {
                return CommonImages.INSTANCE.edit();
            }
        };

        column.setFieldUpdater(new FieldUpdater<KSessionModel, ImageResource>() {
            @Override
            public void update(int index, KSessionModel model, ImageResource value) {
                presenter.onOptionsSelectedForKSessions(model);
            }
        });

        column.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

        grid.addColumn(column);
        grid.setColumnWidth(column, "40px");
    }

    private void setUpRemoveColumn() {
        ClickableImageResourceCell typeImageCell = new ClickableImageResourceCell(true);
        TooltipCellDecorator<ImageResource> decorator = new TooltipCellDecorator<ImageResource>(typeImageCell);
        decorator.setText(ProjectEditorResources.CONSTANTS.Delete());
        Column<KSessionModel, ImageResource> column = new Column<KSessionModel, ImageResource>(decorator) {
            @Override
            public ImageResource getValue(KSessionModel model) {
                return CommonImages.INSTANCE.DeleteItemSmall();
            }
        };

        column.setFieldUpdater(new FieldUpdater<KSessionModel, ImageResource>() {
            @Override
            public void update(int index, KSessionModel model, ImageResource value) {
                presenter.onDelete(model);
            }
        });

        column.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

        grid.addColumn(column);
        grid.setColumnWidth(column, "40px");
    }

    @Override
    public void showOptionsPopUp(KSessionModel kSessionModel) {
        kSessionModelOptionsPopUp.show(kSessionModel);
    }

    @Override
    public void makeReadOnly() {
        addButton.setEnabled(false);
    }

    @Override
    public void makeEditable() {
        addButton.setEnabled(true);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setItemList(List<KSessionModel> list) {
        grid.setRowData(list);
    }

    @UiHandler("addButton")
    public void onAddClicked(ClickEvent event) {
        presenter.onAdd();
    }

}
