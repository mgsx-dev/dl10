package net.mgsx.dl10.engine.inputs;

public abstract class TriggerBase<T extends ControllerBase> {

	public abstract boolean isOn(T controller);

	public abstract String format();
}
