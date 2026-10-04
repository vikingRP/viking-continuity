package me.pepperbell.continuity.client.resource;

import me.pepperbell.continuity.client.util.BooleanState;

public final class InvalidIdentifierStateHolder {
	private static final ThreadLocal<BooleanState> LOOKUP = ThreadLocal.withInitial(BooleanState::new);

	public static BooleanState get() {
		return LOOKUP.get();
	}
}
