/*
 * This file is part of the RUNA WFE project.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; version 2.1
 * of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */
package ru.runa.wf.web.tag;

import java.util.List;
import org.apache.ecs.html.Form;
import org.apache.ecs.html.Input;
import org.apache.ecs.html.TD;
import org.tldgen.annotations.BodyContent;
import ru.runa.af.web.BatchPresentationUtils;
import ru.runa.common.WebResources;
import ru.runa.common.web.PagingNavigationHelper;
import ru.runa.common.web.html.RadioButtonTdBuilder;
import ru.runa.common.web.html.ReflectionRowBuilder;
import ru.runa.common.web.html.RowBuilder;
import ru.runa.common.web.html.SortingHeaderBuilder;
import ru.runa.common.web.html.TableBuilder;
import ru.runa.common.web.html.TdBuilder;
import ru.runa.common.web.tag.BatchReturningTitledFormTag;
import ru.runa.wf.web.MessagesProcesses;
import ru.runa.wf.web.action.ShowDefinitionHistoryDiffAction;
import ru.runa.wf.web.html.PropertiesProcessTdBuilder;
import ru.runa.wf.web.html.UndeployProcessDefinitionTdBuilder;
import ru.runa.wfe.definition.DefinitionHistoryClassPresentation;
import ru.runa.wfe.definition.dto.WfDefinition;
import ru.runa.wfe.presentation.BatchPresentation;
import ru.runa.wfe.service.DefinitionService;
import ru.runa.wfe.service.delegate.Delegates;

@org.tldgen.annotations.Tag(bodyContent = BodyContent.JSP, name = "listDefinitionsHistoryForm")
public class ListDefinitionsHistoryFormTag extends BatchReturningTitledFormTag {

    private static final long serialVersionUID = 2203850190079109329L;

    @Override
    protected void fillFormElement(TD tdFormElement) {
        DefinitionService definitionService = Delegates.getDefinitionService();
        BatchPresentation batchPresentation = getBatchPresentation();
        int count = definitionService.getProcessDefinitionsCount(getUser(), batchPresentation);
        List<WfDefinition> definitions = definitionService.getDeployments(getUser(), batchPresentation, true);
        PagingNavigationHelper navigation = new PagingNavigationHelper(pageContext, batchPresentation, count, "/definitions_history.do");
        navigation.addPagingNavigationTable(tdFormElement);
        TdBuilder[] builders = BatchPresentationUtils.getBuilders(
                new TdBuilder[] { new RadioButtonTdBuilder("version1", "version"), new RadioButtonTdBuilder("version2", "version") }, 
                batchPresentation,
                new TdBuilder[] { new UndeployProcessDefinitionTdBuilder(), new PropertiesProcessTdBuilder() });
        SortingHeaderBuilder headerBuilder = new SortingHeaderBuilder(batchPresentation, 2, 2, getReturnAction(), pageContext, false);
        RowBuilder rowBuilder = new ReflectionRowBuilder(definitions, batchPresentation, pageContext, WebResources.ACTION_MAPPING_MANAGE_DEFINITION,
                getReturnAction(), new DefinitionUrlStrategy(pageContext), builders);
        tdFormElement.addElement(new TableBuilder().build(headerBuilder, rowBuilder));
        navigation.addPagingNavigationTable(tdFormElement);
        int nameFieldIndex = batchPresentation.getType().getFieldIndex(DefinitionHistoryClassPresentation.NAME);
        String definitionName = batchPresentation.getFieldFilteredCriteria(nameFieldIndex).getFilterTemplate(0);
        tdFormElement.addElement(new Input(Input.HIDDEN, ShowDefinitionHistoryDiffAction.DEFINITION_NAME, definitionName));
        tdFormElement.addElement(new Input(Input.HIDDEN, ShowDefinitionHistoryDiffAction.NUM_CONTEXT_LINES, "3"));
    }

    @Override
    protected boolean isSubmitButtonVisible() {
        return true;
    }

    @Override
    protected boolean isSubmitButtonEnabled() {
        return true;
    }

    @Override
    protected String getTitle() {
        return MessagesProcesses.TITLE_PROCESS_DEFINITIONS.message(pageContext);
    }

    @Override
    public String getAction() {
        return ShowDefinitionHistoryDiffAction.ACTION;
    }

    @Override
    public String getMethod() {
        return Form.GET;
    }

    @Override
    protected String getSubmitButtonName() {
        return MessagesProcesses.BUTTON_VIEW_DIFFERENCES.message(pageContext);
    }

}
