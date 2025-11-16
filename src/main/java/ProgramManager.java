import org.json.JSONArray;
import org.json.JSONObject;

public class ProgramManager {
    private int programId;
    private Exception callbackException = null;

    public int findOrCreateProgramId(SDKManager sdk, Terminal terminal) {
        findProgramId(sdk, terminal);

        if (programId == 0) {
            createProgram(sdk, terminal);
        }

        return programId;
    }

    private void findProgramId(SDKManager sdk, Terminal terminal) {
        programId = 0;

        ViplexCore.CallBack callBack = (code, data) -> {
            JSONObject obj = new JSONObject(data);
            JSONArray programList = obj.getJSONArray("programList");

            for (int i = 0; i < programList.length(); i++) {
                if (programList.getJSONObject(i).getString("name").equals(terminal.getSn())) {
                    programId = programList.getJSONObject(i).getInt("programID");
                    break;
                }
            }

            AsyncHelper.setApiReturn(true);
        };

        sdk.getViplexCore().nvGetProgramAsync("", callBack);
        AsyncHelper.waitAPIReturn();
    }

    private void createProgram(SDKManager sdk, Terminal terminal) {
        callbackException = null;

        ViplexCore.CallBack callBack = (code, data) -> {
            try {
                if (code != 0) {
                    throw new RuntimeException();
                }

                JSONObject obj = new JSONObject(data);
                programId = obj.getJSONObject("onSuccess").getInt("programID");
            } catch (Exception e) {
                callbackException = e;
            } finally {
                AsyncHelper.setApiReturn(true);
            }
        };

        JSONObject obj = TemplateLoader.load("create-program.json");
        obj.put("name", terminal.getSn());
        obj.put("width", terminal.getWidth());
        obj.put("height", terminal.getHeight());

        sdk.getViplexCore().nvCreateProgramAsync(obj.toString(), callBack);
        AsyncHelper.waitAPIReturn();

        if (callbackException != null) {
            throw new RuntimeException("프로그램 생성 오류", callbackException);
        }
    }
}
