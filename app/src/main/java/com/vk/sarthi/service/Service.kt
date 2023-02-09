package com.vk.sarthi.service

import com.vk.sarthi.model.*
import com.vk.sarthi.utli.Constants
import com.vk.sarthi.utli.com.vk.sarthi.model.MessageListResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface Service {

    @POST(Constants.getcoordinatorcomplaints)
    suspend fun getComplaintList(@Body req:CoordinatoridMode): Response<ComplaintResponse>

    @POST(Constants.loginUrl)
    suspend fun loginUrl(@Body req: LoginReq): Response<LoginResponse>


    @POST(Constants.forgotpassword)
    suspend fun forgetPassword(@Body req: ForgetPasswordOTPReq): Response<OTPRespose>

    @POST(Constants.resetpassword)
    suspend fun verifyPassword(@Body req: VerifyPassword): Response<LoginResponse>


    @Multipart
    @POST(Constants.addComplaintComment)
    suspend fun createComment(
        @PartMap partMap: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part file: MultipartBody.Part?
    ): Response<AddCommentResponse>

    @POST(Constants.getOfficeWorkList)
    suspend fun getOfficeWorkList(@Body complaintReq: ComplaintReq): Response<OfficeWorkResponse>


    @Multipart
    @POST(Constants.createWork)
    suspend fun createWorkTask(
        @PartMap partMap: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part file: MultipartBody.Part?
    ): Response<CreateOfficeWorkResponse>

    @POST(Constants.deleteComment)
    suspend fun deleteComment(@Body deleteCommentModel: DeleteCommentModel):Response<CreateOfficeWorkResponse>

    @POST(Constants.deleteworkcomment)
    suspend fun deleteOfficeWork(@Body deleteCommentModel: DeleteOfficeWorkModel):Response<CreateOfficeWorkResponse>

    @Multipart
    @POST(Constants.createDailyvisit)
    suspend fun createDailyVisit(
        @PartMap partMap: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part birthdayFileBody: MultipartBody.Part?,
        @Part rashanshopinfoBody: MultipartBody.Part?,
        @Part electricInfoFileBody: MultipartBody.Part?,
        @Part drinkingwaterinfofileBody: MultipartBody.Part?,
        @Part watercanelinfofileBody: MultipartBody.Part?,
        @Part schoolinfofileBody: MultipartBody.Part?,
        @Part primaryHealthInfoFileBody: MultipartBody.Part?,
        @Part vetarnityHealthInfoFileBody: MultipartBody.Part?,
        @Part govInfoInfoFileBody: MultipartBody.Part?,
        @Part politicalInfoFileBody: MultipartBody.Part?,
        @Part deathPersonInfoFileBody: MultipartBody.Part?,
        @Part newschemesfileBody: MultipartBody.Part?,
        @Part devinfofileBody: MultipartBody.Part?,
        @Part otherInfoFileBody: MultipartBody.Part?
    ):Response<DailyVisitResponse>

    @POST(Constants.getDailyvisit)
    suspend fun getDailyVisitListFetch(@Body model: ComplaintReq):Response<DailyVisitListResponse>

    @POST(Constants.deleteDailyWork)
    suspend fun deleteDailyWork(@Body deleteOfficeWorkModel: DeleteDailyVisitModel): Response<CreateOfficeWorkResponse>

    @POST(Constants.getvillages)
    suspend fun getVillageList(@Body model:VillageReq): Response<VilageReponse>


    @POST(Constants.getYojnaList)
    suspend fun getYojnaList(): Response<YojnaReponse>

    @Headers("Content-Type: application/json")
    @POST(Constants.implementyojana)
    suspend fun sendYojna(@Body model: YojsnaPostReq): Response<YojnaResponse>

    @POST("api/getcoordinatormsglist")
    suspend fun getMsgList(@Body complaintReq: CoordinatoridMode): Response<MessageListResponse>
}