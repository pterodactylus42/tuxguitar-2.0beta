package org.herac.tuxguitar.ui.event;

import org.herac.tuxguitar.ui.UIComponent;
import org.herac.tuxguitar.ui.resource.UIPosition;

public class UIMouseWheelEvent extends UIMouseEvent {
	
	private Integer value;
	
	public UIMouseWheelEvent(UIComponent control, UIPosition position, Integer button, Integer state, Integer value) {
		super(control, position, button, state);
		
		this.value = value;
	}

	public Integer getValue() {
		return value;
	}
}
