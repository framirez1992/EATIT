package far.com.eatit.Interfases;

public interface IActualizationListener {
    void onError(String msg);
    void onProgressChange(int currentProgress, String msg);
    void onFinishLoad();

}
