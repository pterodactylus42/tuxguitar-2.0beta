package org.herac.tuxguitar.app.view.dialog.keybindings;

import java.util.ArrayList;
import java.util.List;

import org.herac.tuxguitar.app.TuxGuitar;
import org.herac.tuxguitar.app.action.impl.settings.TGReloadLanguageAction;
import org.herac.tuxguitar.app.action.impl.settings.TGReloadSettingsAction;
import org.herac.tuxguitar.app.system.keybindings.KeyBindingAction;
import org.herac.tuxguitar.app.system.keybindings.KeyBindingActionDefaults;
import org.herac.tuxguitar.app.ui.TGApplication;
import org.herac.tuxguitar.app.view.controller.TGViewContext;
import org.herac.tuxguitar.app.view.dialog.helper.TGOkCancelDefaults;
import org.herac.tuxguitar.app.view.util.TGDialogUtil;
import org.herac.tuxguitar.editor.action.TGActionProcessor;
import org.herac.tuxguitar.ui.UIFactory;
import org.herac.tuxguitar.ui.event.UIMouseDoubleClickListener;
import org.herac.tuxguitar.ui.event.UIMouseEvent;
import org.herac.tuxguitar.ui.layout.UITableLayout;
import org.herac.tuxguitar.ui.resource.UIKeyCombination;
import org.herac.tuxguitar.ui.widget.UITable;
import org.herac.tuxguitar.ui.widget.UITableItem;
import org.herac.tuxguitar.ui.widget.UIWindow;
import org.herac.tuxguitar.util.TGKeyBindFormatter;

public class TGKeyBindingEditor {
	
	private TGViewContext context;
	private UIWindow dialog;
	private UITable<KeyBindingAction> table;
	private List<KeyBindingAction> kbActions;
	
	public TGKeyBindingEditor(TGViewContext context){
		this.context = context;
		this.kbActions = new ArrayList<KeyBindingAction>();
	}
	
	public void show() {
		final UIFactory uiFactory = TGApplication.getInstance(context.getContext()).getFactory();
		final UIWindow uiParent = context.getAttribute(TGViewContext.ATTRIBUTE_PARENT);
		final UITableLayout dialogLayout = new UITableLayout();
		
		this.dialog = uiFactory.createWindow(uiParent, true, true);
		this.dialog.setLayout(dialogLayout);
		this.dialog.setText(TuxGuitar.getProperty("key-bindings-editor"));
		
		this.table = uiFactory.createTable(this.dialog, true);
		this.table.setColumns(2);
		this.table.setColumnName(0, TuxGuitar.getProperty("key-bindings-editor-action-column"));
		this.table.setColumnName(1, TuxGuitar.getProperty("key-bindings-editor-shortcut-column"));
		this.table.addMouseDoubleClickListener(new UIMouseDoubleClickListener() {
			public void onMouseDoubleClick(UIMouseEvent event) {
				final KeyBindingAction kbAction = TGKeyBindingEditor.this.table.getSelectedValue();
				if( kbAction != null ){
					TGKeyBindingSelector keyBindingSelector = new TGKeyBindingSelector(TGKeyBindingEditor.this, kbAction, new TGKeyBindingSelectorHandler() {
						public void handleSelection(UIKeyCombination kb) {
							TGKeyBindingEditor.this.removeKeyBindingAction(kb);
							kbAction.setCombination(kb);
							TGKeyBindingEditor.this.updateTableItems();
						}
					});
					keyBindingSelector.select(TGKeyBindingEditor.this.dialog);
				}
			}
		});
		dialogLayout.set(this.table, 1, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, true);
		dialogLayout.set(this.table, UITableLayout.MAXIMUM_PACKED_WIDTH, 500f);
		dialogLayout.set(this.table, UITableLayout.PACKED_HEIGHT, 250f);
		
		this.loadCurrentKeyBindingActions();
		
		//------------------BUTTONS--------------------------
		TGOkCancelDefaults okCancelDefaults = new TGOkCancelDefaults(context.getContext(), uiFactory, this.dialog,
				new Runnable() {
					public void run() {
						save();
						TGKeyBindingEditor.this.dialog.dispose();
					}
				},
				new Runnable() {
					public void run() {
						TGKeyBindingEditor.this.dialog.dispose();
					}
				},
				new Runnable() {
					public void run() {
						TGKeyBindingEditor.this.loadDefaultKeyBindingActions();
					}
				});
		dialogLayout.set(okCancelDefaults.getControl(), 2, 1, UITableLayout.ALIGN_FILL, UITableLayout.ALIGN_FILL, true, false);

		dialog.computePackedSize(null, null);
		dialog.setMinimumSize(dialog.getPackedSize());
		TGDialogUtil.openDialog(this.dialog,TGDialogUtil.OPEN_STYLE_CENTER | TGDialogUtil.OPEN_STYLE_PACK);
	}
	
	public void createKeyBindingActions(List<KeyBindingAction> keyBindingActions) {
		this.kbActions.clear();
		
		List<String> actionIds = TuxGuitar.getInstance().getActionAdapterManager().getKeyBindingActionIds().getActionIds();
		for(String actionId : actionIds) {
			this.kbActions.add(new KeyBindingAction(actionId, this.findKeyBinding(keyBindingActions, actionId)));
		}
	}
	
	public UIKeyCombination findKeyBinding(List<KeyBindingAction> keyBindingActions, String actionId) {
		for(KeyBindingAction keyBindingAction : keyBindingActions) {
			if( keyBindingAction.getAction().equals(actionId)){
				return (UIKeyCombination) keyBindingAction.getCombination().clone();
			}
		}
		return null;
	}
	
	public void loadCurrentKeyBindingActions() {
		this.createKeyBindingActions(TuxGuitar.getInstance().getKeyBindingManager().getKeyBindingActions());
		this.updateTableItems();
	}
	
	public void loadDefaultKeyBindingActions() {
		this.createKeyBindingActions(KeyBindingActionDefaults.getDefaultKeyBindings(getContext().getContext()));
		this.updateTableItems();
	}
	
	public void updateTableItems() {
		KeyBindingAction selection = this.table.getSelectedValue();
		
		this.table.removeItems();
		TGKeyBindFormatter formatter = TGKeyBindFormatter.getInstance();
		for(KeyBindingAction kbAction : this.kbActions) {
			UITableItem<KeyBindingAction> item = new UITableItem<KeyBindingAction>(kbAction);
			item.setText(0, TuxGuitar.getProperty(kbAction.getAction()));
			item.setText(1, (kbAction.getCombination() != null ? formatter.format(kbAction.getCombination().getKeyStrings()) : ""));
			
			this.table.addItem(item);
		}
		this.table.setSelectedValue(selection);
	}
	
	public KeyBindingAction findKeyBindingAction(UIKeyCombination kb){
		if( kb != null ){
			for(KeyBindingAction kbAction : this.kbActions){
				if( kb.equals(kbAction.getCombination())){
					return kbAction;
				}
			}
		}
		return null;
	}
	
	public void removeKeyBindingAction(UIKeyCombination kb){
		KeyBindingAction kbAction = this.findKeyBindingAction(kb);
		if( kbAction != null ){
			kbAction.setCombination(null);
		}
	}
	
	public boolean exists(UIKeyCombination kb){
		KeyBindingAction kbAction = this.findKeyBindingAction(kb);
		
		return (kbAction != null);
	}
	
	public void save(){
		List<KeyBindingAction> list = new ArrayList<KeyBindingAction>();
		for(KeyBindingAction kbAction : this.kbActions){
			if( kbAction.getAction() != null && kbAction.getCombination() != null){
				list.add(kbAction);
			}
		}
		
		TuxGuitar.getInstance().getKeyBindingManager().reset(list);
		TuxGuitar.getInstance().getKeyBindingManager().saveKeyBindings();
		
		TGActionProcessor tgActionProcessor = new TGActionProcessor(this.context.getContext(), TGReloadLanguageAction.NAME);
		tgActionProcessor.setAttribute(TGReloadSettingsAction.ATTRIBUTE_FORCE, true);
		tgActionProcessor.process();
	}
	
	public TGViewContext getContext() {
		return this.context;
	}
	
	public boolean isDisposed(){
		return (this.dialog == null || this.dialog.isDisposed());
	}
}
