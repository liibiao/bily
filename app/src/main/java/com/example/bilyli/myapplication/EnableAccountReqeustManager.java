package com.example.bilyli.myapplication;

import com.gw.plugin.domain.communicate.ui.receive.event.config.RequestCheckVerifiCodeEvent;
import com.gw.plugin.domain.communicate.ui.receive.event.config.RequestNewCustomerVerifiCodeEvent;
import com.gw.plugin.domain.communicate.ui.send.event.config.ResponseCheckVerifiCodeEvent;
import com.gw.plugin.domain.communicate.ui.send.event.config.ResponseNewCustomerEvent;
import com.gw.plugin.domain.communicate.ui.send.event.config.ResponseNewCustomerVerifiCodeEvent;
import com.gw.plugin.domain.communicate.ui.send.event.config.base.BaseResponseConfigEvent;
import com.gwtsz.android.rxbus.RxBus;
import com.gwtsz.android.rxbus.Subscribe;
import com.gwtsz.android.rxbus.ThreadMode;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * @author bily.li
 * 激活账户业务数据请求管理器
 */
public class EnableAccountReqeustManager {
    /**获取验证码*/
    private static final Integer NEW_CUSTOMER_VERIFI_CODE = 1;
    /**校验验证码*/
    private static final Integer CHECK_VERIFI_CODE = 2;
    /**开户激活*/
    private static final Integer NEW_CUSTOMER = 3;

    private Map<Integer, WeakReference<EnableAcountResponse>> responseMap = new HashMap<>();

    private EnableAccountReqeustManager(){
        RxBus.getInstance().register(this);
    }

    private static class SingletonHandler {
        private static EnableAccountReqeustManager singleton = new EnableAccountReqeustManager();
    }

    public static EnableAccountReqeustManager getInstance(){
        return SingletonHandler.singleton;
    }

    /**
     * 获取验证码
     */
    public void requestNewCustomerVerifiCode(String phone, EnableAcountResponse<ResponseNewCustomerVerifiCodeEvent> response){
        responseMap.put(NEW_CUSTOMER_VERIFI_CODE, new WeakReference<EnableAcountResponse>(response));
        RequestNewCustomerVerifiCodeEvent verifiCodeEvent = new RequestNewCustomerVerifiCodeEvent();
        verifiCodeEvent.setSeq(NEW_CUSTOMER_VERIFI_CODE);
        Map<String, Object> params = new HashMap<>();
        params.put("_mobileNumber", "18777771495");
        verifiCodeEvent.setJsonParam(paramToJson(params));
        RxBus.getInstance().post(verifiCodeEvent);
    }

    /**
     * 验证验证码
     */
    public void requestCheckVerifiCode(String id, String code, EnableAcountResponse<ResponseCheckVerifiCodeEvent> checkVerifiCodeEvent){
        responseMap.put(CHECK_VERIFI_CODE, new WeakReference<EnableAcountResponse>(checkVerifiCodeEvent));
        RequestCheckVerifiCodeEvent verifiCodeEvent = new RequestCheckVerifiCodeEvent();
        verifiCodeEvent.setSeq(CHECK_VERIFI_CODE);
        Map<String, Object> params = new HashMap<>();
        params.put("_id", "555");
        params.put("_verifiCode", code);
        verifiCodeEvent.setJsonParam(paramToJson(params));
        RxBus.getInstance().post(verifiCodeEvent);
    }

    /**
     * 激活账户
     */
    public void requestNewCustomer(String mobilePhone, String idCard, String token, String pwd, String email, String chinessName, EnableAcountResponse<ResponseNewCustomerEvent> newCustomerEvent){
        responseMap.put(NEW_CUSTOMER, new WeakReference<EnableAcountResponse>(newCustomerEvent));
        RequestCheckVerifiCodeEvent verifiCodeEvent = new RequestCheckVerifiCodeEvent();
        verifiCodeEvent.setSeq(NEW_CUSTOMER);
        String json = "customer=" +
                "{\"passwordRaw\":\""+pwd+"\"," +
                "\"email\":\""+email+"\"," +
                "\"_token\":\""+token+"\"," +
                "\"idDocumentNumber\":{\"value\":\""+idCard+"\"}," +
                "\"chineseName\":\""+chinessName+"\"," +
                "\"mobilePhone\":\""+mobilePhone+"\"}" +
                "&accounts=[{\"accountLevel\":\"MIN\",\"currency\":\"USD\"}]";
        verifiCodeEvent.setJsonParam(json);
        RxBus.getInstance().post(verifiCodeEvent);
    }

    private String paramToJson(Map<String, Object> params){
        if(params != null){
            JSONObject object = new JSONObject();
            for(Map.Entry<String, Object> me : params.entrySet()){
                try {
                    object.put(me.getKey(), me.getValue());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return object.toString();
        }
        return "";
    }

    /**
     * 获取验证码响应
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    protected void onResponseNewCustomerVerifiCode(ResponseNewCustomerVerifiCodeEvent verifiCodeEvent){
        toResponse(verifiCodeEvent);
    }

    /**
     * 验证验证码响应
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    protected void onResponseCheckVerifiCodeEvent(ResponseCheckVerifiCodeEvent checkVerifiCodeEvent){
        toResponse(checkVerifiCodeEvent);
    }

    /**
     * 开户激活
     * @param newCustomerEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    protected void onResponseNewCustomerEvent(ResponseNewCustomerEvent newCustomerEvent){
        toResponse(newCustomerEvent);
    }

    private <T extends BaseResponseConfigEvent>void toResponse(T sep) {
        WeakReference<EnableAcountResponse> response = responseMap.get(sep.getSeq());
        if(response == null){
            return;
        }
        EnableAcountResponse<T> responseNewCustomerVerifiCode = response.get();
        if(responseNewCustomerVerifiCode != null){
            responseNewCustomerVerifiCode.onResponse(sep);
        }
    }

    public void release(){
        RxBus.getInstance().unRegister(this);
    }

    public interface EnableAcountResponse<T extends BaseResponseConfigEvent>{
        void onResponse(T t);
    }
}
