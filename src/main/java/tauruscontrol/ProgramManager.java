package tauruscontrol;

import org.json.JSONArray;
import org.json.JSONObject;
import tauruscontrol.domain.media.MediaManager;
import tauruscontrol.domain.terminal.Terminal;
import tauruscontrol.sdk.SDKManager;
import tauruscontrol.sdk.ViplexCore;

public class ProgramManager {
    private int programId;
    private Exception callbackException = null;

    public void editProgram(SDKManager sdk, MediaManager mediaManager) {
        ViplexCore.CallBack callBack = (code, data) -> {
            try {
                if (code != 0) {
                    throw new RuntimeException(code + ": " + data);
                }
            } finally {
                AsyncHelper.setApiReturn(true);
            }
        };

        JSONObject obj = TemplateLoader.load("edit-program.json");
        obj.put("programID", programId);
        obj.put("pageInfo", mediaManager.buildWidgetContainers());

        sdk.getViplexCore().nvSetPageProgramAsync(obj.toString(), callBack);
        AsyncHelper.waitAPIReturn();
    }

    public void saveProgramToLocal(SDKManager sdk) {
        ViplexCore.CallBack callBack = (code, data) -> {
            try {
                if (code != 0) {
                    throw new RuntimeException(code + ": " + data);
                }
            } finally {
                AsyncHelper.setApiReturn(true);
            }
        };

        JSONObject obj = TemplateLoader.load("save-program.json");
        obj.put("programID", programId);
        obj.put("outPutPath", "temp/program");

        sdk.getViplexCore().nvMakeProgramAsync(obj.toString(), callBack);
        AsyncHelper.waitAPIReturn();
    }

    public void publishProgram(SDKManager sdk, Terminal terminal, MediaManager mediaManager) {
        ViplexCore.CallBack callBack = (code, data) -> {
            try {
                if (code == 65362) {
                    JSONObject obj = new JSONObject(data);
                    System.out.printf("프로그램 전송 진행률: %d%%\n",
                            (obj.getLong("m_curBytes") * 100) / obj.getLong("m_totalBytes"));
                    return;
                }

                if (code == 0) {
                    System.out.println("프로그램 전송 완료");
                    return;
                }

                // 나머지 코드 예외 처리
                throw new RuntimeException(code + ": " + data);
            } finally {
                AsyncHelper.setApiReturn(true);
            }
        };

        JSONObject obj = TemplateLoader.load("publish.json");
        obj.put("sn", terminal.getSn());
        JSONObject filePaths = obj.getJSONObject("sendProgramFilePaths");
        filePaths.put("programPath", System.getProperty("user.dir") + "/temp/program/program1");
        filePaths.put("mediasPath", mediaManager.buildMediasPath());

        sdk.getViplexCore().nvStartTransferProgramAsync(obj.toString(), callBack);
        AsyncHelper.waitAPIReturn();
    }

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
