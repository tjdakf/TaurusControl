package tauruscontrol.service;

import org.json.JSONArray;
import org.json.JSONObject;
import tauruscontrol.util.AsyncHelper;
import tauruscontrol.util.TemplateLoader;
import tauruscontrol.domain.media.MediaManager;
import tauruscontrol.domain.terminal.Terminal;
import tauruscontrol.sdk.SDKManager;
import tauruscontrol.sdk.ViplexCore;

import java.util.function.Consumer;

public class ProgramManager {
    private static final String PROGRAM_OUTPUT_PATH = "temp/program";

    private final SDKManager sdk;
    private int programId;
    private Exception callbackException = null;

    public ProgramManager() {
        this.sdk = SDKManager.getInstance();
    }

    public void editProgram(MediaManager mediaManager) {
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

    public void saveProgramToLocal() {
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
        obj.put("outPutPath", PROGRAM_OUTPUT_PATH);

        sdk.getViplexCore().nvMakeProgramAsync(obj.toString(), callBack);
        AsyncHelper.waitAPIReturn();
    }

    public void publishProgram(Terminal terminal, MediaManager mediaManager, Consumer<Integer> onProgress) {
        callbackException = null;

        ViplexCore.CallBack callBack = (code, data) -> {
            try {
                if (code == 65362) {
                    JSONObject obj = new JSONObject(data);
                    int progress = (int) ((obj.getLong("m_curBytes") * 100) / obj.getLong("m_totalBytes"));
                    if (onProgress != null) {
                        onProgress.accept(progress);
                    }
                    return;
                }

                if (code == 0) {
                    AsyncHelper.setApiReturn(true);
                    return;
                }

                callbackException = new RuntimeException(code + ": " + data);
            } catch (Exception e) {
                callbackException = e;
            } finally {
                if (code != 65362) {
                    AsyncHelper.setApiReturn(true);
                }
            }
        };

        JSONObject obj = TemplateLoader.load("publish.json");
        obj.put("sn", terminal.getSn());
        JSONObject filePaths = obj.getJSONObject("sendProgramFilePaths");
        filePaths.put("programPath", System.getProperty("user.dir") + "/temp/program/program" + programId);
        filePaths.put("mediasPath", mediaManager.buildMediasPath());

        sdk.getViplexCore().nvStartTransferProgramAsync(obj.toString(), callBack);
        AsyncHelper.waitAPIReturn();

        // Main 스레드에서 예외 체크
        if (callbackException != null) {
            throw new RuntimeException("프로그램 전송 실패", callbackException);
        }
    }

    public int findOrCreateProgramId(Terminal terminal) {
        findProgramId(terminal);

        if (programId == 0) {
            createProgram(terminal);
        }

        return programId;
    }

    private void findProgramId(Terminal terminal) {
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

    private void createProgram(Terminal terminal) {
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
