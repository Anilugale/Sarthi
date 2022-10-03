package com.vk.sarthi.service

import com.vk.sarthi.model.*
import com.vk.sarthi.utli.Constants
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface Service {

    @POST(Constants.getcoordinatorcomplaints)
    suspend fun getComplaintList(@Body req:ComplaintReq): Response<ComplaintResponse>

    @POST(Constants.loginUrl)
    suspend fun loginUrl(@Body req: LoginReq): Response<LoginResponse>


    @POST(Constants.forgotpassword)
    suspend fun forgetPassword(@Body req: ForgetPasswordOTPReq): Response<LoginResponse>

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

    @POST(Constants.createDailyvisit)
    suspend fun createDailyVisit(@Body model: DailyVisitReqModel):Response<DailyVisitResponse>

    @POST(Constants.getDailyvisit)
    suspend fun getDailyVisitListFetch(@Body model: ComplaintReq):Response<DailyVisitListResponse>

    @POST(Constants.deleteDailyWork)
    suspend fun deleteDailyWork(@Body deleteOfficeWorkModel: DeleteDailyVisitModel): Response<CreateOfficeWorkResponse>

    @POST(Constants.getvillages)
    suspend fun getVillageList(): Response<VilageReponse>

}