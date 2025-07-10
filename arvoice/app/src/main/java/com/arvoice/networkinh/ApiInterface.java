package com.arvoice.networkinh;



import com.arvoice.model.CarListModel;
import com.arvoice.model.CurrentUserModel;
import com.arvoice.model.LeadCreate;
import com.arvoice.model.LeadsFilter;
import com.arvoice.model.LeadsListModel;
import com.arvoice.model.LoginRequest;
import com.arvoice.model.LoginResponse;
import com.arvoice.model.SummaryModel;
import com.arvoice.model.TaskListModel;
import com.arvoice.model.TransitionModel;
import com.arvoice.model.UserModel;
import com.arvoice.model.UserSettingsModel;
import com.arvoice.utils.Constant;

import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET(Constant.getCurrentUser)
    Call<CurrentUserModel> getCurrentUser(@Header("Authorization") String token);

    @GET(Constant.getSummary)
    Call<List<SummaryModel.SummaryItem>> getFeedCategory(@Header("Authorization") String token);

    @GET(Constant.getAdvanceSummary)
    Call<Map<String, String>> getAdvancedSummary(
            @Header("Authorization") String token,
            @Query("templateId") int templateId,
            @Query("summaryBy") String summaryBy
    );

    @GET(Constant.getTemplate)
    Call<LeadsFilter> getTemplate(
            @Query("pageId") String pageId,
            @Header("Authorization") String token

    );

    @GET(Constant.getTransition + "/{taskId}")
    Call<List<TransitionModel.TaskItem>> getTransition(
            @Path("taskId") int taskId,
            @Header("Authorization") String token
    );


    @GET(Constant.getTasks)
    Call<List<TaskListModel.TaskItem>> getTask(
            @Header("Authorization") String token
    );
    @GET(Constant.getCars)
    Call<List<CarListModel.CarItem>> getCarList(
            @Header("Authorization") String token
    );

    @GET(Constant.getUserSettings)
    Call<List<UserSettingsModel.UserSettingItem>> getUserSettings(
            @Header("Authorization") String token
    );

    @GET(Constant.getUsers)
    Call<List<UserModel>> getUsers(@Header("Authorization") String token);

    @POST(Constant.getLeads)
    Call<LeadsListModel> getLeads(
            @Header("Authorization") String token,
            @Query("page") String page,
            @Query("size") String size,
            @Query("sortBy") String sortBy,
            @Query("sortDir") String sortDir,
            @Body RequestBody body
    );

    @POST(Constant.addLeads)
    Call<String> addLeads(
            @Header("Authorization") String token,
            @Body RequestBody body
    );

    @POST(Constant.mobileSettings)
    Call<String> createMobileSettings(
            @Header("Authorization") String token,
            @Body RequestBody body
    );

    @POST(Constant.addTask)
    Call<String> addTask(
            @Header("Authorization") String token,
            @Body RequestBody body
    );

    @PUT("{leadId}")
    Call<String> updateLeads(@Header("Authorization") String token,
                             @Path("leadId") int leadId,
                             @Body RequestBody body);

    @PUT(Constant.updateTask + "/{taskId}")
    Call<String> updateTask(@Header("Authorization") String token,
                             @Path("taskId") int taskId,
                             @Body RequestBody body);


    @GET(Constant.getLeadForm)
    Call<LeadCreate> getLeadForm(@Header("Authorization") String token);

    @Headers({
            "Content-Type: application/json",
            "X-Client-Type: Mobile"
    })
    @POST(Constant.loginURL)
    Call<LoginResponse> login(@Body RequestBody body);



}