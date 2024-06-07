package ir.tic.clouddc.event;

public interface EventAction {

    public void registerEvent();

    public void updateEvent();

    public void endEvent();
}
