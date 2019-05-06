package org.herac.tuxguitar.app.view.toolbar.main;

import org.herac.tuxguitar.editor.action.track.TGAddNewTrackAction;
import org.herac.tuxguitar.editor.action.track.TGRemoveTrackAction;
import org.herac.tuxguitar.player.base.MidiPlayer;
import org.herac.tuxguitar.ui.widget.UIButton;

public class TGMainToolBarSectionTrack extends TGMainToolBarSection {
	
	private UIButton add;
	private UIButton remove;
	
	public TGMainToolBarSectionTrack(TGMainToolBar toolBar) {
		super(toolBar);
	}
	
	public void createSection() {
		this.add = this.createButton();
		this.add.addSelectionListener(this.createActionProcessor(TGAddNewTrackAction.NAME));
		
		this.remove = this.createButton();
		this.remove.addSelectionListener(this.createActionProcessor(TGRemoveTrackAction.NAME));
		
		this.loadIcons();
		this.loadProperties();
	}
	
	public void loadProperties(){
		this.add.setToolTipText(this.getText("track.add"));
		this.remove.setToolTipText(this.getText("track.remove"));
	}
	
	public void loadIcons(){
		this.add.setImage(this.getIconManager().getTrackAdd());
		this.remove.setImage(this.getIconManager().getTrackRemove());
	}
	
	public void updateItems(){
		boolean running = MidiPlayer.getInstance(this.getToolBar().getContext()).isRunning();
		this.add.setEnabled(!running);
		this.remove.setEnabled(!running);
	}
}
