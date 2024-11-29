package com.appointment.tutionservice

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface TutionServiceApi {

    @POST("userRegLogin")
    fun registrationStepOne(
        @Body registrationData: RegistrationData,
    ): Call<APiModel>

    @POST("userTypeUpdate")
    fun userTypeUpdate(
        @Body userTypeData: UserTypeData,
    ): Call<UserTypeResponse>

    @POST("userLogin")
    fun loginWithMobileNo(
        @Body loginMobileNoModel: LoginMobileNoModel,
    ): Call<APiModel>

    @POST("isCheckReferralId")
    fun isCheckReferralId(
        @Body referralData: ReferralRequest,
    ): Call<ReferralCodeResponse>

    @POST("jobDealAction")
    fun jobDealAction(
        @Body jobDealAction: JobDealAction,
    ): Call<UpdateProfileResponse>

    @POST("updateProfileVerification")
    fun updateProfileVerification(
        @Body jobDealAction: ProfileVerifyPaymentRequest,
    ): Call<UpdateProfileResponse>

    @POST("verifyOtp")
    fun checkOtpForLogin(
        @Body otpModel: OtpModel,
    ): Call<OtpResponse>

    @POST("serviceComplain")
    fun sendServiceComplain(
        @Body sendServiceRequest: SendServiceRequest,
    ): Call<ServiceResponse>

    @POST("appCrmDocument")
    fun appCrmDocument(
        @Body crmCreateData: CrmCreateData,
    ): Call<UpdateProfileResponse>

    @POST("appCustomerProfileUpdate")
    fun customerProfileUpdate(
        @Body customerProfileData: CustomerProfileUpdateData,
    ): Call<UpdateProfileResponse>

    @POST("updateMembershipPackage")
    fun updateMembershipPackage(
        @Body updatePackageRequest: UpdatePackageRequest,
    ): Call<UpdateProfileResponse>

    @POST("getServiceComplain")
    fun getServiceComplain(
        @Body supportComplain: ServiceComplainData,
    ): Call<SupportComplainResponse>

    @POST("changeJobStatus")
    fun changeJobStatus(
        @Body jobStatusChange: JobStatusChange,
    ): Call<UpdateProfileResponse>

    @POST("rechargeWallet")
    fun rechargeWallet(
        @Body paymentRequest: PaymentRequest,
    ): Call<UpdateProfileResponse>

    @POST("appSaveTodoStatus")
    fun appSaveTodoStatus(
        @Body todoItem: TodoItem,
    ): Call<UpdateProfileResponse>

    @Multipart
    @POST("appUserProfileUpdate")
    fun providerProfileUpdate(
        @Part("user_profile_name") user_profile_name: RequestBody,
        @Part("services") services: RequestBody,
        @Part("app_user_type") app_user_type: RequestBody,
        @Part("user_email") user_email: RequestBody,
        @Part("user_mobile") user_mobile: RequestBody,
        @Part("package_id") package_id: RequestBody,
        @Part("validity_days") validity_days: RequestBody,
        @Part("package_price") package_price: RequestBody,
        @Part("business_name") business_name: RequestBody,
        @Part("about_business") about_business: RequestBody,
        @Part("ifsc_code") ifsc_code: RequestBody,
        @Part("account_number") account_number: RequestBody,
        @Part("branch_name") branch_name: RequestBody,
        @Part("bank_name") bank_name: RequestBody,
        @Part("addhar_no") addhar_no: RequestBody,
        @Part("optional_mobile_no") optional_mobile_no: RequestBody,
        @Part("franchise_no") franchise_no: RequestBody,
        @Part("gst_no") gst_no: RequestBody,
        @Part("landline_no") landline_no: RequestBody,
        @Part("registration_no") registration_no: RequestBody,
        @Part("fax_no") fax_no: RequestBody,
        @Part("working_hours") working_hours: RequestBody,
        @Part("facebook_profile") facebook_profile: RequestBody,
        @Part("tweeter_profile") tweeter_profile: RequestBody,
        @Part("gplus_profile") gplus_profile: RequestBody,
        @Part("linkedin_profile") linkedin_profile: RequestBody,
        @Part("bank_info_id") bank_info_id: RequestBody,
        @Part("api_key") api_key: RequestBody,
        @Part("device_id") device_id: RequestBody,
        @Part("device_type") device_type: RequestBody,
        @Part("device_token") device_token: RequestBody,
        @Part("app_user_id") app_user_id: RequestBody,
        @Part("app_version") app_version: RequestBody,
        @Part("app_user_key") app_user_key: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("lng") lng: RequestBody,
        @Part image: MultipartBody.Part,
        @Part("website") website: RequestBody,
        @Part("description") description: RequestBody,
        @Part("experience") experience: RequestBody,
        @Part("days") days: RequestBody,
        @Part logo_image_name: MultipartBody.Part,
        @Part("logo_image_id") logo_image_id: RequestBody,
        @Part discount_banner_name: MultipartBody.Part,
        @Part("discount_banner_id") discount_banner_id: RequestBody,

        ): Call<ProviderProfileResponse>

    @POST("appUserAddressUpdate")
    fun appUserAddressUpdate(
        @Body providerAddressData: ProviderAddressData,
    ): Call<ProviderAddressResponse>

    @POST("appUserServicesUpdate")
    fun appUserServicesUpdate(
        @Body serviceCategoryResponse: ServiceCategoryResponse,
    ): Call<UpdateProfileResponse>

    @POST("getJobBidPersonByServiceProvider")
    fun getJobBidPersonByServiceProvider(
        @Body jobPostRequest: JobPostRequest,
    ): Call<JobBidResponse>

    @POST("appSaveTodoData")
    fun appSaveTodoData(
        @Body todoListSendData: TodoListSendData,
    ): Call<TodoResponseData>

    @POST("jobBidByServiceProvider")
    fun jobBidByServiceProvider(
        @Body bidData: BidData,
    ): Call<UpdateProfileResponse>

    @POST("customerJobPost")
    fun customerJobPost(
        @Body jobPost: JobPost,
    ): Call<JobPostResponse>

    @POST("sendJobPostMessage")
    fun sendJobPostMessage(
        @Body messageData: JobPostMessage,
    ): Call<MessageResponse>

    @POST("saveKeyword")
    fun saveKeyword(
        @Body searchKeyword: SearchKeyword,
    ): Call<KeywordResponse>

    @POST("appUserReview")
    fun appUserReview(
        @Body review: Review,
    ): Call<UpdateProfileResponse>

    @POST("appCrmDocumentDelete")
    fun appCrmDocumentDelete(
        @Body crmProfileDelete: CrmProfileDelete,
    ): Call<UpdateProfileResponse>


    @Multipart
    @POST("appUserProfileImageUpdate")
    fun appUserProfileImageUpdate(
        @Part("user_mobile") userMobile: RequestBody,
        @Part("api_key") apiKey: RequestBody,
        @Part("device_id") deviceId: RequestBody,
        @Part("device_type") deviceType: RequestBody,
        @Part("device_token") deviceToken: RequestBody,
        @Part("app_user_id") appUserId: RequestBody,
        @Part("app_version") appVersion: RequestBody,
        @Part("app_user_key") appUserKey: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("lng") lng: RequestBody,
        @Part profile_image: MultipartBody.Part
    ): Call<ProfileImageResponse>

    @Multipart
    @POST("uploadServiceProviderBanner")
    fun uploadServiceProviderBanner(
        @Part("user_mobile") userMobile: RequestBody,
        @Part("api_key") apiKey: RequestBody,
        @Part("device_id") deviceId: RequestBody,
        @Part("device_type") deviceType: RequestBody,
        @Part("device_token") deviceToken: RequestBody,
        @Part("app_user_id") appUserId: RequestBody,
        @Part("app_version") appVersion: RequestBody,
        @Part("app_user_key") appUserKey: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("lng") lng: RequestBody,
        @Part provider_banner: MultipartBody.Part
    ): Call<ProviderGalleryResponse>

    @Multipart
    @POST("appCrmDocumentSendSms")
    fun sendSmsToUser(
        @Part("message") message: RequestBody,
        @Part("user_name") userName: RequestBody,
        @Part("user_mobile") userMobile: RequestBody,
        @Part("send_sms_mobile") sendSmsMobile: RequestBody,
        @Part("user_id") userId: RequestBody,
        @Part("type") type: RequestBody,
        @Part("api_key") apiKey: RequestBody,
        @Part("device_id") deviceId: RequestBody,
        @Part("device_type") deviceType: RequestBody,
        @Part("device_token") deviceToken: RequestBody,
        @Part("app_user_id") appUserId: RequestBody,
        @Part("app_version") appVersion: RequestBody,
        @Part("app_user_key") appUserKey: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("lng") lng: RequestBody,
        @Part push_image: MultipartBody.Part?
    ): Call<MessageAPiResponse>

    @Multipart
    @POST("appUserDocumentUpload")
    fun providerDocumentUpload(
        @Part("user_mobile") userMobile: RequestBody,
        @Part("api_key") apiKey: RequestBody,
        @Part("device_id") deviceId: RequestBody,
        @Part("device_type") deviceType: RequestBody,
        @Part("device_token") deviceToken: RequestBody,
        @Part("app_user_id") appUserId: RequestBody,
        @Part("app_version") appVersion: RequestBody,
        @Part("app_user_key") appUserKey: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("lng") lng: RequestBody,
        @Part personal_id_image_name: MultipartBody.Part?,
        @Part("personal_id_image_id") personalIdImageId: RequestBody,
        @Part bank_proof_image_name: MultipartBody.Part?,
        @Part("bank_proof_image_id") bankProofImageId: RequestBody,
        @Part business_id_image_name: MultipartBody.Part?,
        @Part("business_id_image_id") businessIdImageId: RequestBody,
        @Part residence_image_name: MultipartBody.Part?,
        @Part("residence_image_id") residenceImageId: RequestBody,
    ): Call<UploadDocResponse>

    @Multipart
    @POST("crmFileImport")
    fun crmFileImport(
        @Part file: MultipartBody.Part?,
        @Part("api_key") apiKey: RequestBody,
        @Part("user_mobile") userMobile: RequestBody,
        @Part("device_id") deviceId: RequestBody,
        @Part("device_token") deviceToken: RequestBody,
        @Part("app_user_id") appUserId: RequestBody,
        @Part("app_user_type") app_user_type: RequestBody,
        @Part("device_type") deviceType: RequestBody,
        @Part("app_user_key") appUserKey: RequestBody,
        @Part("app_version") appVersion: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("lng") lng: RequestBody,
    ): Call<UpdateProfileResponse>

    @GET("generalSettingInfo/api_key/trzp86b5se8vhn4f5t9e")
    suspend fun allTheData(): Response<CompanyData>

    @GET("dashboardApi/user_mobile/{userMobile}/api_key/{apiKey}/device_id/{deviceId}/device_token/{deviceToken}/app_user_key/{appUserKey}/app_user_id/{appUserId}/app_version/{appVersion}")
    fun getBanner(
        @Path("userMobile") userMobile: String,
        @Path("apiKey") apiKey: String,
        @Path("deviceId") deviceId: String,
        @Path("deviceToken") deviceToken: String,
        @Path("appUserKey") appUserKey: String,
        @Path("appUserId") appUserId: String,
        @Path("appVersion") appVersion: String
    ): Call<ApiResponse>

    @GET("getAppUserUploadedDocuments/user_mobile/{userMobile}/api_key/{apiKey}/device_id/{deviceId}/device_token/{deviceToken}/app_user_key/{appUserKey}/app_user_id/{appUserId}/app_version/{appVersion}")
    fun getAppUserUploadedDocuments(
        @Path("userMobile") userMobile: String,
        @Path("apiKey") apiKey: String,
        @Path("deviceId") deviceId: String,
        @Path("deviceToken") deviceToken: String,
        @Path("appUserKey") appUserKey: String,
        @Path("appUserId") appUserId: String,
        @Path("appVersion") appVersion: String
    ): Call<DocumentResponse>

    @GET("getAllServicesData/user_mobile/{userMobile}/api_key/{apiKey}/device_id/{deviceId}/device_token/{deviceToken}/app_user_key/{appUserKey}/app_user_id/{appUserId}/app_version/{appVersion}")
    fun getAllServicesData(
        @Path("userMobile") userMobile: String,
        @Path("apiKey") apiKey: String,
        @Path("deviceId") deviceId: String,
        @Path("deviceToken") deviceToken: String,
        @Path("appUserKey") appUserKey: String,
        @Path("appUserId") appUserId: String,
        @Path("appVersion") appVersion: String
    ): Call<SearchBarResponse>

    @GET("walletHistory/user_mobile/{userMobile}/api_key/{apiKey}/device_id/{deviceId}/device_token/{deviceToken}/app_user_key/{appUserKey}/app_user_id/{appUserId}/app_version/{appVersion}")
    fun getWalletHistoryData(
        @Path("userMobile") userMobile: String,
        @Path("apiKey") apiKey: String,
        @Path("deviceId") deviceId: String,
        @Path("deviceToken") deviceToken: String,
        @Path("appUserKey") appUserKey: String,
        @Path("appUserId") appUserId: String,
        @Path("appVersion") appVersion: String
    ): Call<WalletResponse>

    @GET("allPushNotificationList/user_mobile/{userMobile}/api_key/{apiKey}/device_id/{deviceId}/device_token/{deviceToken}/app_user_key/{appUserKey}/app_user_id/{appUserId}/app_version/{appVersion}")
    fun allPushNotificationList(
        @Path("userMobile") userMobile: String,
        @Path("apiKey") apiKey: String,
        @Path("deviceId") deviceId: String,
        @Path("deviceToken") deviceToken: String,
        @Path("appUserKey") appUserKey: String,
        @Path("appUserId") appUserId: String,
        @Path("appVersion") appVersion: String
    ): Call<AlertNotifyResponse>

    @GET("updatePushNotification/user_mobile/{userMobile}/api_key/{apiKey}/device_id/{deviceId}/device_token/{deviceToken}/app_user_key/{appUserKey}/app_user_id/{appUserId}/notification_id/{notificationId}/app_version/{appVersion}")
    fun updatePushNotification(
        @Path("userMobile") userMobile: String,
        @Path("apiKey") apiKey: String,
        @Path("deviceId") deviceId: String,
        @Path("deviceToken") deviceToken: String,
        @Path("appUserKey") appUserKey: String,
        @Path("appUserId") appUserId: String,
        @Path("notificationId") notificationId: String,
        @Path("appVersion") appVersion: String
    ): Call<UpdateNotificationResponse>

    @GET("getAllNotifications/user_mobile/{userMobile}/api_key/{apiKey}/device_id/{deviceId}/device_token/{deviceToken}/app_user_key/{appUserKey}/app_user_id/{appUserId}/app_version/{appVersion}")
    fun getAllNotifications(
        @Path("userMobile") userMobile: String,
        @Path("apiKey") apiKey: String,
        @Path("deviceId") deviceId: String,
        @Path("deviceToken") deviceToken: String,
        @Path("appUserKey") appUserKey: String,
        @Path("appUserId") appUserId: String,
        @Path("appVersion") appVersion: String
    ): Call<NotificationResponse>

    @GET("getCrmDocumentListing/user_mobile/{userMobile}/api_key/{apiKey}/device_id/{deviceId}/device_token/{deviceToken}/app_user_key/{appUserKey}/app_user_id/{appUserId}/app_version/{appVersion}")
    fun getCrmDocumentListing(
        @Path("userMobile") userMobile: String,
        @Path("apiKey") apiKey: String,
        @Path("deviceId") deviceId: String,
        @Path("deviceToken") deviceToken: String,
        @Path("appUserKey") appUserKey: String,
        @Path("appUserId") appUserId: String,
        @Path("appVersion") appVersion: String
    ): Call<CrmListResponse>


    @GET("getAllMembershipPackages/user_mobile/{userMobile}/api_key/{apiKey}/device_id/{deviceId}/device_token/{deviceToken}/app_user_key/{appUserKey}/app_user_id/{appUserId}/app_version/{appVersion}")
    fun getAllMembershipPackages(
        @Path("userMobile") userMobile: String,
        @Path("apiKey") apiKey: String,
        @Path("deviceId") deviceId: String,
        @Path("deviceToken") deviceToken: String,
        @Path("appUserKey") appUserKey: String,
        @Path("appUserId") appUserId: String,
        @Path("appVersion") appVersion: String
    ): Call<MembershipPackagesResponse>

    @GET("getAppUserProfile/user_mobile/{userMobile}/api_key/{apiKey}/device_id/{deviceId}/device_token/{deviceToken}/app_user_key/{appUserKey}/app_user_id/{appUserId}/app_version/{appVersion}")
    fun getCustomerProfileDetails(
        @Path("userMobile") userMobile: String,
        @Path("apiKey") apiKey: String,
        @Path("deviceId") deviceId: String,
        @Path("deviceToken") deviceToken: String,
        @Path("appUserKey") appUserKey: String,
        @Path("appUserId") appUserId: String,
        @Path("appVersion") appVersion: String
    ): Call<CustomerProfileResponse>

    @GET("appUserAddressInfo/user_mobile/{userMobile}/api_key/{apiKey}/device_id/{deviceId}/device_token/{deviceToken}/app_user_key/{appUserKey}/app_user_id/{appUserId}/app_version/{appVersion}")
    fun appUserAddressInfo(
        @Path("userMobile") userMobile: String,
        @Path("apiKey") apiKey: String,
        @Path("deviceId") deviceId: String,
        @Path("deviceToken") deviceToken: String,
        @Path("appUserKey") appUserKey: String,
        @Path("appUserId") appUserId: String,
        @Path("appVersion") appVersion: String
    ): Call<GetLocationResponse>

    @GET("videoTutoriaList/user_mobile/{userMobile}/api_key/{apiKey}/device_id/{deviceId}/device_token/{deviceToken}/app_user_key/{appUserKey}/app_user_id/{appUserId}/app_version/{appVersion}")
    fun videoTutoriaList(
        @Path("userMobile") userMobile: String,
        @Path("apiKey") apiKey: String,
        @Path("deviceId") deviceId: String,
        @Path("deviceToken") deviceToken: String,
        @Path("appUserKey") appUserKey: String,
        @Path("appUserId") appUserId: String,
        @Path("appVersion") appVersion: String
    ): Call<TutorialResponse>

    @GET("getAppUserServices/user_mobile/{userMobile}/api_key/{apiKey}/device_id/{deviceId}/device_token/{deviceToken}/app_user_key/{appUserKey}/app_user_id/{appUserId}/app_version/{appVersion}")
    fun getProviderProfileDetails(
        @Path("userMobile") userMobile: String,
        @Path("apiKey") apiKey: String,
        @Path("deviceId") deviceId: String,
        @Path("deviceToken") deviceToken: String,
        @Path("appUserKey") appUserKey: String,
        @Path("appUserId") appUserId: String,
        @Path("appVersion") appVersion: String
    ): Call<UserData>

    @GET("getServiceProviderExtraInfo/user_mobile/{userMobile}/api_key/{apiKey}/device_token/{deviceToken}/app_user_key/{appUserKey}/app_user_id/{appUserId}/app_version/{appVersion}")
    fun getServiceProviderExtraInfo(
        @Path("userMobile") userMobile: String,
        @Path("apiKey") apiKey: String,
        @Path("deviceToken") deviceToken: String,
        @Path("appUserKey") appUserKey: String,
        @Path("appUserId") appUserId: String,
        @Path("appVersion") appVersion: String
    ): Call<ProviderDetailsResponse>

    @GET("getAppCustomerServices/user_mobile/{userMobile}/api_key/{apiKey}/device_id/{deviceId}/device_token/{deviceToken}/app_user_key/{appUserKey}/app_user_id/{appUserId}/app_version/{appVersion}")
    fun getAppCustomerServices(
        @Path("userMobile") userMobile: String,
        @Path("apiKey") apiKey: String,
        @Path("deviceId") deviceId: String,
        @Path("deviceToken") deviceToken: String,
        @Path("appUserKey") appUserKey: String,
        @Path("appUserId") appUserId: String,
        @Path("appVersion") appVersion: String
    ): Call<SelectedServiceResponse>

    @GET("getTodoListing/user_mobile/{userMobile}/api_key/{apiKey}/device_id/{deviceId}/device_token/{deviceToken}/app_user_key/{appUserKey}/app_user_id/{appUserId}/app_version/{appVersion}")
    fun getTodoListing(
        @Path("userMobile") userMobile: String,
        @Path("apiKey") apiKey: String,
        @Path("deviceId") deviceId: String,
        @Path("deviceToken") deviceToken: String,
        @Path("appUserKey") appUserKey: String,
        @Path("appUserId") appUserId: String,
        @Path("appVersion") appVersion: String
    ): Call<TodoGetResponse>

    @GET("getCustomerJobListing/user_mobile/{userMobile}/api_key/{apiKey}/device_id/{deviceId}/device_token/{deviceToken}/app_user_key/{appUserKey}/app_user_id/" +
            "{appUserId}/app_version/{appVersion}/job_post_status/{jobPostStatus}/jp_month/{jpMonth}/jp_year/{jpYear}/offset/{offSet}")
    fun getCustomerJobListing(
        @Path("userMobile") userMobile: String,
        @Path("apiKey") apiKey: String,
        @Path("deviceId") deviceId: String,
        @Path("deviceToken") deviceToken: String,
        @Path("appUserKey") appUserKey: String,
        @Path("appUserId") appUserId: String,
        @Path("appVersion") appVersion: String,
        @Path("jobPostStatus") jobPostStatus: String,
        @Path("jpMonth") jpMonth: String,
        @Path("jpYear") jpYear: String,
        @Path("offSet") offSet: String
    ): Call<BookingApiResponse>

    @GET("userUsedReferralId/user_mobile/{userMobile}/api_key/{apiKey}/device_id/{deviceId}/device_token/{deviceToken}/app_user_key/{appUserKey}/app_user_id/" +
            "{appUserId}/app_version/{appVersion}")
    fun getUserUsedReferralId(
        @Path("userMobile") userMobile: String,
        @Path("apiKey") apiKey: String,
        @Path("deviceId") deviceId: String,
        @Path("deviceToken") deviceToken: String,
        @Path("appUserKey") appUserKey: String,
        @Path("appUserId") appUserId: String,
        @Path("appVersion") appVersion: String,
    ): Call<ReferralIdResponse>

    @GET("getServiceProviderJobListing/user_mobile/{userMobile}/api_key/{apiKey}/device_id/{deviceId}/device_token/{deviceToken}/app_user_key/{appUserKey}/app_user_id/" +
            "{appUserId}/app_version/{appVersion}/job_post_status/{jobPostStatus}/jp_month/{jpMonth}/jp_year/{jpYear}/offset/{offSet}")
    fun getServiceProviderJobListing(
        @Path("userMobile") userMobile: String,
        @Path("apiKey") apiKey: String,
        @Path("deviceId") deviceId: String,
        @Path("deviceToken") deviceToken: String,
        @Path("appUserKey") appUserKey: String,
        @Path("appUserId") appUserId: String,
        @Path("appVersion") appVersion: String,
        @Path("jobPostStatus") jobPostStatus: String,
        @Path("jpMonth") jpMonth: String,
        @Path("jpYear") jpYear: String,
        @Path("offSet") offSet: String
    ): Call<BookingApiResponse>

    @GET("getServiceDetails/user_mobile/{userMobile}/api_key/{apiKey}/device_id/{deviceId}/device_token/{deviceToken}/app_user_key/{appUserKey}/app_user_id/{appUserId}/app_version/{appVersion}/service_id/{serviceId}")
    fun getServiceDetails(
        @Path("userMobile") userMobile: String,
        @Path("apiKey") apiKey: String,
        @Path("deviceId") deviceId: String,
        @Path("deviceToken") deviceToken: String,
        @Path("appUserKey") appUserKey: String,
        @Path("appUserId") appUserId: String,
        @Path("appVersion") appVersion: String,
        @Path("serviceId") serviceId: String,
    ): Call<QuestionnaireResponse>

    @GET("getServiceDetails/user_mobile/{userMobile}/api_key/{apiKey}/device_id/{deviceId}/device_token/{deviceToken}/app_user_key/{appUserKey}/app_user_id/{appUserId}/app_version/{appVersion}/service_id/{serviceId}/bookapt/{bookapt}")
    fun getBookAppointment(
        @Path("userMobile") userMobile: String,
        @Path("apiKey") apiKey: String,
        @Path("deviceId") deviceId: String,
        @Path("deviceToken") deviceToken: String,
        @Path("appUserKey") appUserKey: String,
        @Path("appUserId") appUserId: String,
        @Path("appVersion") appVersion: String,
        @Path("serviceId") serviceId: String,
        @Path("bookapt") bookapt: String,
    ): Call<BusinessResponse>

    @GET("getServiceNameByType/user_mobile/{userMobile}/api_key/{apiKey}/device_id/{deviceId}/device_token/{deviceToken}/app_user_key/{appUserKey}/app_user_id/{appUserId}/app_version/{appVersion}/cat_type/{catType}/sub_type/{subType}/param/{param}")
    fun getServiceNameByType(
        @Path("userMobile") userMobile: String,
        @Path("apiKey") apiKey: String,
        @Path("deviceId") deviceId: String,
        @Path("deviceToken") deviceToken: String,
        @Path("appUserKey") appUserKey: String,
        @Path("appUserId") appUserId: String,
        @Path("appVersion") appVersion: String,
        @Path("catType") catType: String,
        @Path("subType") subType: String,
        @Path("param") param: String
    ): Call<ServiceProviderResponse>

    @GET(
        "serviceProviderDashboard/user_mobile/{userMobile}/api_key/{apiKey}/device_id/{deviceId}/app_user_key/{appUserKey}/app_user_id/{appUserId}/app_version/{appVersion}/offset/{offset}/job_app_flg/{jobAppFlg}/job_post_title/{jobPostTitle}/job_post_date/{jobPostDate}/job_post_location/{jobPostLocation}/postalCode/{postalCode}/search_val/{searchVal}"
    )
    fun getServiceProviderDashboard(
        @Path("userMobile") userMobile: String,
        @Path("apiKey") apiKey: String,
        @Path("deviceId") deviceId: String,
        @Path("appUserKey") appUserKey: String,
        @Path("appUserId") appUserId: String,
        @Path("appVersion") appVersion: String,
        @Path("offset") offset: String,
        @Path("jobAppFlg") jobAppFlg: String,
        @Path("jobPostTitle") jobPostTitle: String,
        @Path("jobPostDate") jobPostDate: String,
        @Path("jobPostLocation") jobPostLocation: String,
        @Path("postalCode") postalCode: String,
        @Path("searchVal") searchVal: String
    ): Call<DashboardProviderResponse>

    @GET(
        "getAppUserMessages/user_mobile/{userMobile}/api_key/{apiKey}/device_id/{deviceId}/device_token/{deviceToken}/app_user_key/{appUserKey}/app_user_id/{appUserId}/app_version/{appVersion}/offset/{offset}"
    )
    fun getAppUserMessages(
        @Path("userMobile") userMobile: String,
        @Path("apiKey") apiKey: String,
        @Path("deviceId") deviceId: String,
        @Path("deviceToken") deviceToken: String,
        @Path("appUserKey") appUserKey: String,
        @Path("appUserId") appUserId: String,
        @Path("appVersion") appVersion: String,
        @Path("offset") offset: String,
    ): Call<UserMessagesResponse>

    @GET(
        "getJobPostMessages/user_mobile/{userMobile}/api_key/{apiKey}/device_id/{deviceId}/device_token/{deviceToken}/app_user_key/{appUserKey}/app_user_id/{appUserId}/app_version/{appVersion}/job_post_id/{jobPostId}/service_provider_id/{serviceProviderId}"
    )
    fun getJobPostMessages(
        @Path("userMobile") userMobile: String,
        @Path("apiKey") apiKey: String,
        @Path("deviceId") deviceId: String,
        @Path("deviceToken") deviceToken: String,
        @Path("appUserKey") appUserKey: String,
        @Path("appUserId") appUserId: String,
        @Path("appVersion") appVersion: String,
        @Path("jobPostId") jobPostId: String,
        @Path("serviceProviderId") serviceProviderId: String
    ): Call<JobPostMessagesResponse>


}