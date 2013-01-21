package varviewer.client.sampleView;

import java.util.ArrayList;
import java.util.List;

import varviewer.client.VarViewer;
import varviewer.client.services.SampleListService;
import varviewer.client.services.SampleListServiceAsync;
import varviewer.shared.SampleInfo;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.SingleSelectionModel;

/**
 * Displays a list of samples in fancy graphical format. 
 * @author brendan
 *
 */
public class SamplesView extends HorizontalPanel {

	private String filterText = null;
	private VarViewer vvParent = null;
	
	public SamplesView(VarViewer parent) {
		this.vvParent = parent;
		initComponents();
	}
	
	/**
	 * Set the text used to search the samples list. Any sample with an id, analysis type, or 
	 * submitter that 'contains' this text will be listed. Setting the text to null disables
	 * all filtering.
	 * @param filterText
	 */
	public void setFilterText(String filterText) {
		this.filterText = filterText;
		
		if (allSamples != null) {
			if (filterText == null) {
				sampleList.setRowData(allSamples);
				return;
			}
			
			List<SampleInfo> samplesToDisplay = new ArrayList<SampleInfo>();
			for(SampleInfo info : allSamples) {
				if (info.getSampleID().contains(filterText)
						|| info.getAnalysisType().contains(filterText)
						|| info.getSubmitter().contains(filterText)) {
					samplesToDisplay.add(info);
				}
			}
			sampleList.setRowData(samplesToDisplay);
		}
		
	}
	
	/**
	 * Set the list of all samples that may be displayed in this widget. The currently
	 * displayed samples may be affected by the current filter, if there is one. 
	 * @param allSamples
	 */
	public void setSampleList(List<SampleInfo> allSamples) {
		this.allSamples = new ArrayList<SampleInfo>();
		this.allSamples.addAll(allSamples);
		sampleList.setRowData(allSamples);
	}
	
	private void initComponents() {
		this.setStylePrimaryName("samplesview");
		leftPanel = new FlowPanel();
		searchBox = new SearchBox(this);
		leftPanel.add(searchBox);
		
		//sampleImage = new Image("images/sampleIcon48.png");
		
		SampleCell sampleCell = new SampleCell();
		sampleList = new CellList<SampleInfo>( sampleCell );
		sampleList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		
		final SingleSelectionModel<SampleInfo> selectionModel = new SingleSelectionModel<SampleInfo>();
		sampleList.setSelectionModel(selectionModel);
		selectionModel.addSelectionChangeHandler(new Handler() {

			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				updateSelectedSample(selectionModel.getSelectedObject());
			}
		});
		
		ScrollPanel sampleSP = new ScrollPanel(sampleList);
		sampleSP.setStylePrimaryName("samplescrollpane");
		
		leftPanel.add(sampleSP);
		this.add(leftPanel);
		
		//Create sample details panel...
		sampleDetailsPanel = new SampleDetailView(this);
		this.add(sampleDetailsPanel);
	}

	protected VarViewer getVarViewer() {
		return vvParent;
	}
	
	/**
	 * Called when a new sample has been selected in the list. We 
	 * update the details panel to show the new sample here. 
	 * @param selectedSample
	 */
	protected void updateSelectedSample(SampleInfo selectedSample) {
		sampleDetailsPanel.setSample(selectedSample);
	}


	public void refreshSampleList() {
		  sampleListService.getSampleList(new AsyncCallback<List<SampleInfo>>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Error retrieving sample list : "+ caught.getMessage());
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(List<SampleInfo> result) {
				setSampleList(result);
			}
			  
		  });
	}


	/**
	 * Renders a single cell in the sample list table. 
	 * @author brendanofallon
	 *
	 */
	static class SampleCell extends AbstractCell<SampleInfo> {

		@Override
		public void render(com.google.gwt.cell.client.Cell.Context context,
				SampleInfo value, SafeHtmlBuilder sb) {
	
			if (value == null)
				return;
			
			sb.appendHtmlConstant("<table class=\"sampletableitem\">");
			sb.appendHtmlConstant("<tr><td rowspan='3'>");
			sb.appendHtmlConstant("<img src=\"images/sampleIcon48.png\" />");
		    sb.appendHtmlConstant("</td>");
			sb.appendHtmlConstant("<td class=\"sampletabletext\">");
			String idStr = "unknown";
			if (value.getSampleID() != null) 
				idStr = value.getSampleID();
		    sb.appendEscaped(idStr);
		    sb.appendHtmlConstant("</td></tr><tr><td class=\"sampletabletextA\">");
		    String analysisTypeStr = "unknown";
		    if (value.getAnalysisType() != null)
		    	analysisTypeStr = value.getAnalysisType();
		    sb.appendEscaped(analysisTypeStr);
		    
		    sb.appendHtmlConstant("</td></tr></table>");
			
		}
		
	}
	
	
	SampleListServiceAsync sampleListService = (SampleListServiceAsync) GWT.create(SampleListService.class);
	private List<SampleInfo> allSamples = null;
	private SearchBox searchBox;
	private FlowPanel leftPanel;
	private SampleDetailView sampleDetailsPanel;
	private CellList<SampleInfo> sampleList;
	
}
