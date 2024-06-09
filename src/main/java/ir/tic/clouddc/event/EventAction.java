package ir.tic.clouddc.event;

public interface EventAction {

    public void registerEvent(EventRegisterForm eventRegisterForm);

    public void updateEvent(EventRegisterForm eventRegisterForm);

    public void endEvent(EventRegisterForm eventRegisterForm);
}
