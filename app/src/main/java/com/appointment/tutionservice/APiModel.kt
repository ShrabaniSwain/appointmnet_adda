package com.appointment.tutionservice

import com.google.gson.annotations.SerializedName

data class APiModel(
    @SerializedName("msg")
    val message: String,

    @SerializedName("msg_title")
    val messageTitle: String,

    @SerializedName("status")
    val status: Int,

    @SerializedName("data")
    val data: Data
)

data class Data(
    val otp: String,
    val app_user_id: Int,
    val app_user_type: String,
)

data class RegistrationData(
    val register_via: String,
    val name: String,
    val email: String,
    val gender: String,
    val city: String,
    val state: String,
    val pincode: String,
    val user_mobile: String,
    val api_key: String,
    val device_id: String,
    val device_type: String,
    val device_token: String,
    val device_model_no: String,
    val device_serial_no: String,
    val device_brand: String,
    val app_version: String?,
    val lat: Double,
    val lng: Double,
    val app_user_id: String
)

data class LoginMobileNoModel(
    val user_mobile: String,
    val device_id: String,
    val api_key: String
)

data class OtpModel(
    val user_otp: String,
    val user_mobile: String,
    val api_key: String,
    val device_id: String,
    val device_type: String,
    val device_token: String,
    val device_model_no: String,
    val device_serial_no: String,
    val device_brand: String,
    val app_version: String,
    val lat: Double,
    val lng: Double,
    val app_user_id: String
)

data class OtpResponse(
    val msg: String,
    val msg_title: String,
    val status: Int,
    val data: OtpData
)

data class OtpData(
    val app_registration_id: String,
    val app_user_key: String,
    val app_user_action_type: String,
    val current_app_user_type: String
)

data class CompanyData(
    val msg: String,
    val status: String,
    val data: CompanyInfo
)

data class CompanyInfo(
    val site_url: String,
    val company_name: String,
    val company_tag: String,
    val company_email: String,
    val company_phone: String,
    val company_fax: String,
    val company_address: String,
    val company_mobile: String,
    val facebook_link: String,
    val linkedin_url: String,
    val twitter_url: String,
    val allow_signup: String,
    val max_device_login: String,
    val help_url: String,
    val i_am_a: String,
    val android_app_version: String,
    val privacy_policy: String,
    val term_condition: String,
    val all_citys: List<City>
)

data class City(
    val city_id: String,
    val city_name: String,
    val state_name: String,
    val status: String
)

data class UserTypeData(
    val app_user_id: String,
    val app_user_type: String,
    val api_key: String
)

data class ApiResponse(
    @SerializedName("msg") val message: String,
    @SerializedName("msg_title") val messageTitle: String,
    @SerializedName("status") val status: Int,
    @SerializedName("data") val data: ApiData
)

data class ApiData(
    @SerializedName("banners") val banners: Banners,
    @SerializedName("business_services") val businessServices: List<BusinessService>,
    @SerializedName("instant_services") val instantServices: List<InstantService>,
    @SerializedName("featured_services") val featuredServices: List<FeaturedService>
)

data class Banners(
    @SerializedName("top") val topBanners: List<Banner>,
    @SerializedName("small") val smallBanners: List<Banner>
)

data class Banner(
    @SerializedName("banner_id") val bannerId: String,
    @SerializedName("banner_name") val bannerName: String,
    @SerializedName("banner_img") val bannerImage: String,
    @SerializedName("service_id") val serviceId: String,
    @SerializedName("link_data") val linkData: String,
    @SerializedName("external_link") val externalLink: String
)

data class BusinessService(
    val service_id: String,
    val service_name: String,
    val service_description: String,
    val service_img_path: String,
    val service_type: String,
    val has_last_level: Boolean,
    val has_question: Boolean
)

data class InstantService(
    val service_id: String,
    val service_name: String,
    val service_description: String,
    val service_img_path: String,
    val service_type: String,
    val has_last_level: Boolean,
    val has_question: Boolean
)

data class FeaturedService(
    val service_id: String,
    val service_name: String,
    val service_description: String,
    val service_img_path: String,
    val service_type: String,
    val radio_view: String
)

data class SendServiceRequest(
    @SerializedName("subject") val subject: String,
    @SerializedName("message") val message: String,
    @SerializedName("user_mobile") val userMobile: String,
    @SerializedName("api_key") val apiKey: String,
    @SerializedName("device_id") val deviceId: String,
    @SerializedName("device_type") val deviceType: String,
    @SerializedName("device_token") val deviceToken: String,
    @SerializedName("app_user_id") val appUserId: String,
    @SerializedName("app_version") val appVersion: String,
    @SerializedName("app_user_key") val appUserKey: String,
    @SerializedName("lat") val latitude: Double,
    @SerializedName("lng") val longitude: Double
)

data class ServiceResponse(
    @SerializedName("msg")
    val message: String,

    @SerializedName("msg_title")
    val messageTitle: String,

    @SerializedName("status")
    val status: Int,

    @SerializedName("data")
    val data: ServiceData
)

data class ServiceData(
    val support_complain_id: Int
)

data class NotificationResponse(
    @SerializedName("msg") val message: String,
    @SerializedName("msg_title") val messageTitle: String,
    @SerializedName("status") val status: Int,
    @SerializedName("data") val data: NotificationData
)

data class NotificationData(
    @SerializedName("notifications") val notifications: List<Notification>
)

data class Notification(
    @SerializedName("notification_id") val notificationId: String,
    @SerializedName("module_id") val moduleId: String,
    @SerializedName("app_user_id") val appUserId: String,
    @SerializedName("notification_type") val notificationType: String,
    @SerializedName("notification_alert_type") val notificationAlertType: String,
    @SerializedName("notification_message") val notificationMessage: String,
    @SerializedName("notification_title") val notificationTitle: String,
    @SerializedName("email_to") val emailTo: String?,
    @SerializedName("mobile_no") val mobileNo: String?,
    @SerializedName("notification_status") val notificationStatus: String,
    @SerializedName("push_image_url") val pushImageUrl: String,
    @SerializedName("additional_data") val additionalData: String,
    @SerializedName("is_seen") val isSeen: String,
    @SerializedName("doc") val date: String
)


data class CustomerProfileResponse(
    @SerializedName("msg") val message: String,
    @SerializedName("msg_title") val messageTitle: String,
    @SerializedName("status") val status: Int,
    @SerializedName("data") val data: CustomerProfileApiData
)

data class CustomerProfileApiData(
    @SerializedName("membership_packages") val membershipPackages: List<Any>,
    @SerializedName("app_user_data") val appUserData: CustomerProfile
)

data class CustomerProfile(
    @SerializedName("app_user_id") val appUserId: String?,
    @SerializedName("package_id") val packageId: String,
    @SerializedName("app_user_key") val appUserKey: String,
    @SerializedName("app_address_id") val appAddressId: String,
    @SerializedName("user_mobile") val userMobile: String,
    @SerializedName("user_email") val userEmail: String,
    @SerializedName("user_profile_name") val userProfileName: String,
    @SerializedName("gender") val gender: String,
    @SerializedName("pin_code") val pinCode: String,
    @SerializedName("city_name") val cityName: String,
    @SerializedName("state_name") val stateName: String,
    @SerializedName("city_id") val cityId: String,
    @SerializedName("user_status") val userStatus: String,
    @SerializedName("profile_image") val profileImage: String
)

data class UserTypeResponse(
    @SerializedName("msg") val message: String,
    @SerializedName("msg_title") val messageTitle: String,
    @SerializedName("status") val status: Int,
    @SerializedName("data") val data: String
)

data class CustomerProfileUpdateData(
    @SerializedName("user_profile_name") val userProfileName: String,
    @SerializedName("user_email") val userEmail: String,
    @SerializedName("gender") val gender: String,
    @SerializedName("city") val city: String,
    @SerializedName("pin_code") val pinCode: String,
    @SerializedName("user_mobile") val userMobile: String,
    @SerializedName("api_key") val apiKey: String,
    @SerializedName("device_id") val deviceId: String,
    @SerializedName("device_type") val deviceType: String,
    @SerializedName("device_token") val deviceToken: String,
    @SerializedName("app_user_id") val appUserId: String,
    @SerializedName("app_version") val appVersion: String,
    @SerializedName("app_user_key") val appUserKey: String,
    @SerializedName("lat") val latitude: Double,
    @SerializedName("lng") val longitude: Double,
    @SerializedName("state") val state: String
)

data class UpdateProfileResponse(
    @SerializedName("msg") val message: String,
    @SerializedName("msg_title") val messageTitle: String,
    val status: Int,
    val data: List<Any>
)

data class ProviderProfileResponse(
    @SerializedName("msg") val message: String,
    @SerializedName("msg_title") val messageTitle: String,
    val status: Int,
    val data: ProviderProfileImage
)

data class ProviderProfileImage(
    val profile_image: String
)

data class ProfileImageResponse(
    @SerializedName("msg")
    val message: String,

    @SerializedName("msg_title")
    val messageTitle: String,

    @SerializedName("status")
    val status: Int,

    @SerializedName("data")
    val data: ProfileData
)

data class ProviderGalleryResponse(
    val status: Int,
    val msg: String,
    val msg_title: String,
    val banner: List<String>
)

data class ProfileData(
    val profile_image: String
)

data class ServiceProviderResponse(
    val status: Int,
    val msg: String,
    val msg_title: String,
    val data: ServiceProviderData
)

data class ServiceProviderData(
    val service_name: List<ServiceItem>
)

data class ServiceItem(
    val img_path: String,
    val service_id: String,
    val isSelected: Boolean,
    val service_name: String,
    val service_description: String,
    val cat_type: String,
    val is_visible: String,
    val is_shown_in_app: String,
    val display_order: String,
    val radio_view: String,
    val has_last_level: Boolean,
    val has_question: Boolean,
)

data class UserData(
    val msg: String,
    val msg_title: String,
    val status: Int,
    val data: UserDataDetail
)

data class UserDataDetail(
    val membership_packages: List<Any>,
    val app_user_data: AppUserData
)

data class VerificationPackage(
    val name: String,
    val code: String,
    val amount: String,
    val valid_days: String
)

data class AppUserData(
    val app_user_id: String,
    val is_profile_verified: String,
    val verification_package: VerificationPackage,
    val app_user_key: String,
    val user_mobile: String,
    val user_email: String,
    val user_profile_name: String,
    val user_status: String,
    val addhar_no: String?,
    val optional_mobile_no: String?,
    val franchise_no: String?,
    val gst_no: String?,
    val landline_no: String?,
    val registration_no: String?,
    val fax_no: String?,
    val working_hours: String?,
    val facebook_profile: String?,
    val tweeter_profile: String?,
    val gplus_profile: String?,
    val linkedin_profile: String?,
    val bank_info_id: String?,
    val ifsc_code: String?,
    val account_number: String?,
    val branch_name: String?,
    val bank_name: String?,
    val photo_gallery_list: List<String>,
    val location: List<Location>,
    val type: String,
    val profile_image: String,
    val business_logo_image: String,
    val business_logo_id: String,
    val business_name: String?,
    val discount_image: String,
    val discount_id: String,
    val experience: String?,
    val description: String?,
    val website: String?,
    val shop_time: List<ShopTime>
)

data class DashboardProviderResponse(
    val msg: String,
    val msg_title: String,
    val status: Int,
    val data: DashboardProviderModel
)

data class DashboardProviderModel(
    @SerializedName("membership_package")
    val membershipPackage: MembershipPackage,
    @SerializedName("membership_package_expiry_date")
    val membershipPackageExpiryDate: String,
    @SerializedName("is_membership_package_expire")
    val isMembershipPackageExpire: Boolean,
    @SerializedName("is_profile_completed")
    val isProfileCompleted: String,
    @SerializedName("active_enquiry")
    val active_enquiry: Int,
    @SerializedName("active_appointment")
    val active_appointment: Int,
    @SerializedName("completed_appointments")
    val completed_appointments: Int,
    @SerializedName("total_customer")
    val total_customer: Int,
    @SerializedName("total_business")
    val total_business: Int,
    @SerializedName("received_payments")
    val received_payments: Int,
    val appoinsments: List<AppointmentProvider>,
    val enquiry: List<Enquiry>
)

data class AppointmentProvider(
    @SerializedName("user_mobile") val userMobile: String,
    @SerializedName("service_image") val service_image: String,
    @SerializedName("user_email") val userEmail: String,
    @SerializedName("job_post_id_") val jobPostId: String,
    @SerializedName("job_post_log_id") val jobPostLogId: String?,
    @SerializedName("service_id") val serviceId: String,
    @SerializedName("service_provider_id") val serviceProviderId: String,
    @SerializedName("customer_id") val customerId: String,
    @SerializedName("app_address_id") val appAddressId: String,
    @SerializedName("job_title") val jobTitle: String,
    @SerializedName("job_post_code") val jobPostCode: String,
    @SerializedName("job_description") val jobDescription: String,
    @SerializedName("job_remarks") val jobRemarks: String,
    @SerializedName("job_expiry_date") val jobExpiryDate: String,
    @SerializedName("customer_budget") val customerBudget: String,
    @SerializedName("job_post_status") val jobPostStatus: String,
    @SerializedName("apply_job_post_status") val applyJobPostStatus: String?,
    @SerializedName("is_transferred") val isTransferred: String,
    @SerializedName("job_priority") val jobPriority: String,
    @SerializedName("doc") val doc: String,
    @SerializedName("dom") val dom: String?,
    @SerializedName("service_name") val serviceName: String,
    @SerializedName("user_profile_name") val userProfileName: String,
    @SerializedName("profile_img_path") val profileImgPath: String?,
    @SerializedName("pin_code") val pinCode: String,
    @SerializedName("city") val city: String,
    @SerializedName("state") val state: String,
    @SerializedName("job_type") val jobType: String,
    @SerializedName("job_message_count") val jobMessageCount: String
)

data class Enquiry(
    @SerializedName("job_post_id") val jobPostId: String,
    @SerializedName("job_post_log_id") val jobPostLogId: String?,
    @SerializedName("service_id") val serviceId: String,
    @SerializedName("service_image") val service_image: String,
    @SerializedName("service_provider_id") val serviceProviderId: String,
    @SerializedName("customer_id") val customerId: String,
    @SerializedName("app_address_id") val appAddressId: String,
    @SerializedName("job_title") val jobTitle: String,
    @SerializedName("job_post_code") val jobPostCode: String,
    @SerializedName("job_description") val jobDescription: String,
    @SerializedName("job_remarks") val jobRemarks: String,
    @SerializedName("job_expiry_date") val jobExpiryDate: String,
    @SerializedName("customer_budget") val customerBudget: String,
    @SerializedName("job_post_status") val jobPostStatus: String,
    @SerializedName("apply_job_post_status") val applyJobPostStatus: String?,
    @SerializedName("is_transferred") val isTransferred: String,
    @SerializedName("job_priority") val jobPriority: String,
    @SerializedName("doc") val doc: String,
    @SerializedName("dom") val dom: String?,
    @SerializedName("service_name") val serviceName: String,
    @SerializedName("user_profile_name") val userProfileName: String,
    @SerializedName("profile_img_path") val profileImgPath: String?,
    @SerializedName("user_mobile") val userMobile: String?,
    @SerializedName("user_email") val userEmail: String?,
    @SerializedName("pin_code") val pinCode: String,
    @SerializedName("city") val city: String,
    @SerializedName("state") val state: String,
    @SerializedName("job_message_count") val jobMessageCount: String
)

data class MembershipPackageDashboard(
    @SerializedName("package_id")
    val packageId: String,
    @SerializedName("package_name")
    val packageName: String,
    @SerializedName("package_type")
    val packageType: String,
    @SerializedName("validity_days")
    val validityDays: String,
    @SerializedName("package_price")
    val packagePrice: String,
    @SerializedName("package_description")
    val packageDescription: String,
    @SerializedName("pre_notif_adv")
    val preNotificationAdvance: String,
    val status: String,
    @SerializedName("allow_contact")
    val allowContact: String,
    @SerializedName("is_featured")
    val isFeatured: String?,
    val doc: String,
    val dom: String?,
)

data class ProviderAddressData(
    val app_address_id: String,
    val locality: String,
    val building_no: String,
    val street_name: String,
    val pin_code: String,
    val city: String,
    val state: String,
    val landmark: String,
    val user_mobile: String,
    val api_key: String,
    val device_id: String,
    val device_type: String,
    val device_token: String,
    val app_user_id: String,
    val app_version: String,
    val app_user_key: String,
    val lat: Double,
    val lng: Double
)

data class ProviderAddressResponse(
    val msg: String,
    val msg_title: String,
    val status: Int,
    val data: AddressData
)

data class AddressData(
    val app_user_address: String
)

data class GetLocationResponse(
    @SerializedName("msg") val msg: String,
    @SerializedName("msg_title") val msgTitle: String,
    @SerializedName("status") val status: Int,
    @SerializedName("data") val data: GetAddressData
)

data class GetAddressData(
    @SerializedName("app_user_addresses") val appUserAddresses: List<GetProviderAddress>
)

data class GetProviderAddress(
    @SerializedName("app_address_id") val appAddressId: String,
    @SerializedName("app_user_id") val appUserId: String,
    @SerializedName("building_no") val buildingNo: String,
    @SerializedName("street_name") val streetName: String,
    @SerializedName("locality") val locality: String,
    @SerializedName("pin_code") val pinCode: String,
    @SerializedName("city") val city: String,
    @SerializedName("state") val state: String,
    @SerializedName("landmark") val landmark: String,
    @SerializedName("address_type") val addressType: String,
    @SerializedName("is_default") val isDefault: String,
    @SerializedName("dom") val dom: String?, // This field can be nullable
    @SerializedName("full_address") val fullAddress: String
)

data class MembershipPackage(
    @SerializedName("package_id") val packageId: String,
    @SerializedName("package_name") val packageName: String,
    @SerializedName("package_type") val packageType: String,
    @SerializedName("validity_days") val validityDays: String,
    @SerializedName("package_price") val packagePrice: String,
    @SerializedName("package_description") val packageDescription: String,
    @SerializedName("pre_notif_adv") val preNotificationAdv: String,
    @SerializedName("status") val status: String,
    @SerializedName("allow_contact") val allowContact: String,
    @SerializedName("is_featured") val isFeatured: String?,
    @SerializedName("doc") val doc: String,
    @SerializedName("dom") val dom: String
)

data class MembershipPackagesResponse(
    @SerializedName("msg") val msg: String,
    @SerializedName("msg_title") val msgTitle: String,
    @SerializedName("status") val status: Int,
    @SerializedName("data") val data: MembershipPackagesData
)

data class MembershipPackagesData(
    @SerializedName("membership_packages") val membershipPackages: List<MembershipPackage>)

data class UpdatePackageRequest(
    val old_package_id: Int,
    var package_id: String,
    val validity_days: String,
    val user_mobile: String,
    val api_key: String,
    val device_id: String,
    val device_type: String,
    val device_token: String,
    val app_user_id: String,
    val app_version: String,
    val app_user_key: String,
    var paymentId: String,
    val lat: Double,
    val lng: Double
)

data class SupportComplainResponse(
    @SerializedName("msg") val msg: String,
    @SerializedName("msg_title") val msgTitle: String,
    @SerializedName("status") val status: Int,
    @SerializedName("data") val data: List<SupportComplain>
)

data class SupportComplain(
    @SerializedName("support_complain_id")
    val supportComplainId: String,
    @SerializedName("app_user_id")
    val appUserId: String,
    @SerializedName("support_complain_type")
    val supportComplainType: String,
    @SerializedName("job_post_code")
    val jobPostCode: String,
    val subject: String,
    val message: String,
    val status: String,
    val doc: String,
    val dom: String?
)

data class ServiceComplainData(
    @SerializedName("user_mobile")
    val userMobile: String,
    @SerializedName("api_key")
    val apiKey: String,
    @SerializedName("device_id")
    val deviceId: String,
    @SerializedName("device_type")
    val deviceType: String,
    @SerializedName("device_token")
    val deviceToken: String,
    @SerializedName("app_user_id")
    val appUserId: String,
    @SerializedName("app_version")
    val appVersion: String,
    @SerializedName("app_user_key")
    val appUserKey: String,
    val lat: Double,
    val lng: Double
)

data class DocumentResponse(
    val msg: String,
    val msg_title: String,
    val status: Int,
    val personal_id: PersonalId,
    val bank_proof: PersonalId,
    val business_id: PersonalId,
    val residence: PersonalId,
    val data: DocumentData
)

data class DocumentData(
    @SerializedName("app_user_documents") val appUserDocuments: AppUserDocuments
)

data class AppUserDocuments(
    @SerializedName("personal_ids") val personalIds: List<PersonalId>
)

data class PersonalId(
    @SerializedName("app_user_document_id") val appUserDocumentId: String,
    @SerializedName("app_user_id") val appUserId: String,
    @SerializedName("document_type") val documentType: String,
    @SerializedName("document_name") val documentName: String,
    @SerializedName("document_img_path") val documentImgPath: String,
    val doc: String
)

data class CrmCreateData(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("mobile")
    val mobile: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("requirements")
    val requirements: String,
    @SerializedName("dob")
    val dob: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("address")
    val address: String,
    @SerializedName("user_mobile")
    val userMobile: String,
    @SerializedName("api_key")
    val apiKey: String,
    @SerializedName("device_id")
    val deviceId: String,
    @SerializedName("device_type")
    val deviceType: String,
    @SerializedName("device_token")
    val deviceToken: String,
    @SerializedName("app_user_id")
    val appUserId: String,
    @SerializedName("app_version")
    val appVersion: String,
    @SerializedName("app_user_key")
    val appUserKey: String,
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lng")
    val lng: Double
)

data class CrmListResponse(
    val msg: String,
    val msg_title: String,
    val status: Int,
    val data: List<CrmList>
)

data class CrmList(
    val id: String,
    val name: String,
    val mobile: String,
    val email: String,
    val requirements: String,
    val dob: String,
    val city: String,
    val address: String,
    val status: Any?,
    val doc: String,
    val dom: String?,
    val docby: String,
    val domby: String?
)

data class Service(
    val service_id: String,
    val service_name: String
)

data class ServiceCategoryResponse(
    val services: List<Service>,
    val app_user_type: String,
    val user_mobile: String,
    val api_key: String,
    val device_id: String,
    val device_type: String,
    val device_token: String,
    val app_user_id: String,
    val app_version: String,
    val app_user_key: String,
    val lat: Double,
    val lng: Double
)

data class Questionnaire(
    val questionnaire_id: String,
    val question: String,
    val type_of_question: String,
    val question_option_values: List<QuestionOptionValue>?,
    val questionnaire_correct_answer: String
)

data class QuestionnaireResponse(
    val status: Int,
    val msg: String,
    val msg_title: String,
    val sub_category: List<Any>,
    val questionnaires: List<Questionnaire>,
    val business: List<Any>
)

data class QuestionOptionValue(
    val value: String
)

data class JobPost(
    val job_title: String,
    val job_date: String,
    val job_time: String,
    val job_description: String,
    val customer_budget: String,
    val job_expiry_date: String,
    val app_address_id: String,
    val service_id: String,
    val job_post_id: String,
    val job_post_status: String,
    val job_priority_id: String,
    val accept_terms: Boolean,
    val questionnaires: List<QuestionnaireAnswer>,
    val user_mobile: String,
    val api_key: String,
    val device_id: String,
    val device_type: String,
    val device_token: String,
    val app_user_id: String,
    val app_version: String,
    val app_user_key: String,
    var paymentId: String,
    val lat: Double,
    val lng: Double,
    val service_provider_id: Int
)

data class QuestionnaireAnswer(
    val questionnaire_id: String,
    val questionnaire_correct_answer: String
)

data class JobPostResponse(
    val msg: String,
    val msg_title: String,
    val status: Int,
    val data: Int
)

data class BookingApiResponse(
    val msg: String,
    val msg_title: String,
    val status: Int,
    val data: BookingData
)

data class BookingData(
    val customer_enquiry_list: List<CustomerEnquiry>,
    val appointment_list: List<Appointment>,
    val job_bid_listing: List<JobBidListing>
)

data class JobBidListing(
    val job_type: String,
    val job_bits: List<JobBit>,
    val service_image: String,
    val job_post_id: String,
    val service_id: String,
    val service_name: String,
    val service_provider_id: String,
    val job_post_log_id: String,
    val customer_id: String,
    val app_address_id: String,
    val job_title: String,
    val job_post_status: String,
    val job_post_code: String,
    val job_description: String,
    val job_remarks: String,
    val job_expiry_date: String,
    val job_duration: String,
    val customer_budget: String,
    val is_transferred: String,
    val job_security_code: String?,
    val job_priority_id: String,
    val doc: String,
    val dom: String?,
    val total_job_applied: String
)

data class CustomerEnquiry(
    val job_type: String,
    val user_mobile: String,
    val user_email: String,
    val user_profile_name: String,
    val get_ques_ans: List<QuestionAnswer>,
    val job_bits: List<JobBit>,
    val service_image: String,
    val job_post_id: String,
    val service_id: String,
    val service_name: String,
    val job_date: String,
    val job_time: String,
    val service_provider_id: String,
    val job_post_log_id: String,
    val customer_id: String,
    val app_address_id: String,
    val job_title: String,
    val job_post_status: String,
    val job_post_code: String,
    val job_description: String,
    val job_remarks: String,
    val job_expiry_date: String,
    val job_duration: String,
    val customer_budget: String,
    val is_transferred: String,
    val job_security_code: String?,
    val job_priority_id: String,
    val doc: String,
    val dom: String?,
    val total_job_applied: String
)

data class QuestionAnswer(
    val question: String,
    val questionnaire_answer: String
)
data class JobBit(
    val job_post_log_id: String,
    val job_post_status: String,
    val remarks: String,
    val fields: String,
    val amount: String
)

data class Appointment(
    val job_type: String,
    val customer_rating_id: String,
    val ratings: String,
    val user_mobile: String,
    val user_email: String,
    val user_profile_name: String,
    val user_address: String,
    val get_ques_ans: List<QuestionAnswer>,
    val service_image: String,
    val job_post_id: String,
    val service_id: String,
    val service_name: String,
    val job_date: String,
    val job_time: String,
    val service_provider_id: String,
    val customer_id: String,
    val app_address_id: String,
    val job_title: String,
    val job_post_status: String,
    val job_post_code: String,
    val job_description: String,
    val job_remarks: String?,
    val job_expiry_date: String,
    val job_duration: String,
    val customer_budget: String,
    val is_transferred: String,
    val job_security_code: String?,
    val job_priority_id: String,
    val doc: String,
    val dom: String?,
    val total_job_applied: String
)

data class BusinessResponse(
    val status: Int,
    val msg: String,
    val msg_title: String,
    val business: List<Business>
)

data class Business(
    val app_user_id: String,
    val service_user_key: String,
    val service_mobile: String,
    val service_name: String,
    val service_description: String,
    val business_name: String,
    val user_email: String,
    val street_name: String,
    val pin_code: String,
    val locality: String,
    val user_profile_name: String,
    val profile_image: String,
    val is_profile_verified: String,
    val rating: Ratings
)

data class MessageResponse(
    val msg: String,
    val msg_title: String,
    val status: Int,
    val data: MessageData
)

data class MessageData(
    val job_post_message_id: Int
)

data class JobPostMessage(
    val job_post_id: String,
    val service_provider_id: String,
    val message: String,
    val job_post_message_id: Int,
    val user_mobile: String,
    val api_key: String,
    val device_id: String,
    val device_type: String,
    val device_token: String,
    val app_user_id: String,
    val app_version: String,
    val app_user_key: String,
    val lat: Double,
    val lng: Double
)

data class UserMessagesResponse(
    val msg: String,
    @SerializedName("msg_title")
    val msgTitle: String,
    val status: Int,
    val data: UserMessagesData
)

data class UserMessagesData(
    @SerializedName("app_user_messages")
    val appUserMessages: List<UserMessage>
)

data class UserMessage(
    @SerializedName("job_post_code")
    val jobPostCode: String,
    @SerializedName("job_post_id")
    val jobPostId: String,
    @SerializedName("from_id")
    val fromId: String,
    @SerializedName("to_id")
    val toId: String,
    val message: String,
    val doc: String,
    @SerializedName("user_profile_name")
    val userProfileName: String,
    @SerializedName("message_count")
    val messageCount: String
)

data class JobPostMessagesResponse(
    val msg: String,
    @SerializedName("msg_title")
    val msgTitle: String,
    val status: Int,
    val data: JobPostMessagesData
)

data class JobPostMessagesData(
    @SerializedName("job_post_messages")
    val jobPostMessages: List<JobPostMessageData>
)

data class JobPostMessageData(
    val message_type: String,
    @SerializedName("job_post_message_id")
    val jobPostMessageId: String,
    @SerializedName("job_post_id")
    val jobPostId: String,
    @SerializedName("from_id")
    val fromId: String,
    @SerializedName("to_id")
    val toId: String,
    val message: String,
    @SerializedName("is_bookmark")
    val isBookmark: String,
    @SerializedName("is_read")
    val isRead: String,
    val doc: String,
    val dom: String?, // Nullable
    @SerializedName("from_name")
    val fromName: String,
    @SerializedName("from_name_image")
    val fromNameImage: String,
    @SerializedName("to_name")
    val toName: String,
    @SerializedName("to_name_image")
    val toNameImage: String,
)

data class TodoListSendData(
    val id: String,
    val name: String,
    val details: String,
    val status: String,
    val job_status: String,
    val user_mobile: String,
    val api_key: String,
    val device_id: String,
    val device_type: String,
    val device_token: String,
    val app_user_id: String,
    val app_version: String,
    val app_user_key: String,
    val lat: Double,
    val lng: Double
)

data class TodoResponseData(
    val msg: String,
    val msg_title: String,
    val status: Int,
    val data: List<Any>
)

data class TodoGetResponse(
    val msg: String,
    val msgTitle: String,
    val status: Int,
    val data: List<TodoGetData>
)

data class TodoGetData(
    val id: String,
    val name: String,
    val details: String,
    val status: String,
    val job_status: String,
    val doc: String,
    val docby: String,
    val dom: String?
)

data class SearchBarResponse(
    val status: Int,
    val msg: String,
    val msg_title: String,
    val data: List<SearchBar>
)

data class SearchBar(
    val service_id: String,
    val parent_id: String,
    val service_name: String,
    val service_description: String,
    val service_img_path: String?,
    val visiting_charge: String,
    val commission_charge: String,
    val cat_type: String,
    val is_visible: String,
    val is_shown_in_app: String,
    val is_featured_service: String,
    val display_order: String,
    val radio_view: String,
    val doc: String,
    val dom: String?,
    val has_question: Int,
)

data class JobPostRequest(
    val job_post_id: String,
    val app_user_type: String,
    val user_mobile: String,
    val api_key: String,
    val device_id: String,
    val device_type: String,
    val device_token: String,
    val app_user_id: String,
    val app_version: String,
    val app_user_key: String,
    val lat: Double,
    val lng: Double
)

data class JobBidResponse(
    val msg: String,
    val msg_title: String,
    val status: Int,
    val data: List<JobPostLog>
)

data class JobPostLog(
    val job_post_log_id: String,
    val user_mobile: String,
    val app_user_id: String,
    val user_profile_name: String,
    val doc: String,
    val profile_image: String?,
    val fields: String?,
    val remarks: String,
    val amount: String?,
    val customer_rating_id: String,
    val ratings: String,
)

data class BidData(
    val job_post_id: String,
    val fields: String,
    val amount: String,
    val remarks: String,
    val app_user_type: String,
    val user_mobile: String,
    val api_key: String,
    val device_id: String,
    val device_type: String,
    val device_token: String,
    val app_user_id: String,
    val app_version: String,
    val app_user_key: String,
    val lat: Double,
    val lng: Double
)

data class UploadDocResponse(
    val msg: String,
    val msg_title: String,
    val status: Int,
    val personal_id: UploadDocument,
    val bank_proof: UploadDocument,
    val business_id: UploadDocument,
    val residence: UploadDocument
)

data class UploadDocument(
    val app_user_document_id: String,
    val app_user_id: String,
    val document_type: String,
    val document_name: String,
    val document_img_path: String,
    val doc: String
)

data class JobStatusChange(
    val job_post_id: String,
    val job_post_status: String,
    val app_user_type: String,
    val user_mobile: String,
    val api_key: String,
    val device_id: String,
    val device_type: String,
    val device_token: String,
    val app_user_id: String,
    val app_version: String,
    val app_user_key: String,
    val lat: Double,
    val lng: Double
)

data class TodoItem(
    val todo_id: String,
    val status: String,
    val user_mobile: String,
    val api_key: String,
    val device_id: String,
    val device_type: String,
    val device_token: String,
    val app_user_id: String,
    val app_version: String,
    val app_user_key: String,
    val lat: Double,
    val lng: Double
)

data class SelectedService(
    val service_id: String,
    val service_name: String,
    val service_type: String,
    val service_img_path: String
)

data class SelectedServiceData(
    val assigned_services: List<SelectedService>
)

data class SelectedServiceResponse(
    val msg: String,
    val msg_title: String,
    val status: Int,
    val data: SelectedServiceData
)

data class SearchKeyword(
    val keyword: String,
    val search_type: String,
    val app_user_type: String?,
    val user_mobile: String,
    val api_key: String,
    val device_id: String,
    val device_type: String,
    val device_token: String,
    val app_user_id: String,
    val app_version: String,
    val app_user_key: String,
    val lat: Double,
    val lng: Double
)

data class KeywordResponse(
    val msg: String,
    val msg_title: String,
    val status: Int,
    val data: String
)

data class SmsRequest(
    val message: String,
    val user_name: String,
    val user_mobile: List<String>,
    val api_key: String,
    val device_id: String,
    val device_type: String,
    val device_token: String,
    val app_user_id: String,
    val app_version: String,
    val app_user_key: String,
    val lat: Double,
    val lng: Double
)

data class JobDealAction(
    val remarks: String,
    val job_post_status: String,
    val job_post_id: String,
    val job_post_log_id: String,
    val service_provider_id: String,
    val job_post_log_status: Int,
    val user_mobile: String,
    val api_key: String,
    val device_id: String,
    val device_type: String,
    val device_token: String,
    val app_user_id: String,
    val app_version: String,
    val app_user_key: String,
    val paymentId: String,
    val lat: Double,
    val lng: Double
)

data class Review(
    val review_from: String,
    val customer_rating_id: String,
    val review_to: String,
    val remarks: String,
    val job_post_id: String,
    val ratings: Int,
    val app_user_type: Int,
    val user_mobile: String,
    val api_key: String,
    val device_id: String,
    val device_type: String,
    val device_token: String,
    val app_user_id: String,
    val app_version: String,
    val app_user_key: String,
    val lat: Double,
    val lng: Double
)


data class Ratings(
    val average_rating: String,
    val total_customers: String,
    val remarks: List<Remark>
)

data class Remark(
    val remarks: String,
    val doc: String,
    val ratings: String,
    val customer_id: String,
    val profile_img_path: String?
)

data class CrmProfileDelete(
    val id: String,
    val user_mobile: String,
    val api_key: String,
    val device_id: String,
    val device_type: String,
    val device_token: String,
    val app_user_id: String,
    val app_version: String,
    val app_user_key: String,
    val lat: Double,
    val lng: Double
)

data class ProviderDetailsResponse(
    val msg: String,
    val msg_title: String,
    val status: Int,
    val data: Details
)

data class Details(
    val customer_ratings: CustomerRatings,
    val app_user_data: AppUserData
)

data class CustomerRatings(
    val average_rating: String,
    val total_customers: String,
    val remarks: List<Remarks>,
    val groupByratingValue: List<RatingGroup>
)

data class Remarks(
    val remarks: String,
    val doc: String,
    val ratings: String,
    val customer_id: String,
    val profile_img_path: String
)

data class RatingGroup(
    val ratings: String,
    val total_rating: String
)
//data class Details(
//    val app_user_data: AppUserData
//)

data class ProviderDetailsData(
    val app_user_id: String,
    val website: String?,
    val description: String?,
    val experience: String?,
    val business_name: String?,
    val is_profile_verified: String,
    val app_user_key: String,
    val user_mobile: String,
    val user_email: String,
    val user_profile_name: String,
    val user_status: String,
    val addhar_no: String?,
    val optional_mobile_no: String?,
    val franchise_no: String?,
    val gst_no: String?,
    val landline_no: String?,
    val registration_no: String?,
    val fax_no: String?,
    val working_hours: String?,
    val facebook_profile: String?,
    val tweeter_profile: String?,
    val gplus_profile: String?,
    val linkedin_profile: String?,
    val bank_info_id: String?,
    val ifsc_code: String?,
    val account_number: String?,
    val branch_name: String?,
    val bank_name: String?,
    val photo_gallery_list: List<String>,
    val location: List<ProviderLocation>,
    val type: String,
    val profile_image: String,
    val business_logo_image: String,
    val total_no_of_ratings: String,
    val ratings_value: String,
    val total_experience: String,
    val direction: String,
    val shop_time: List<ShopTime>,
    val discount_image: String
)

data class ProviderLocation(
    val city_name: String,
    val state_name: String,
    val city_id: String,
    val pin_code: String
)


data class Location(
    val city_name: String?,
    val state_name: String?,
    val city_id: String?,
    val pin_code: String
)

data class ShopTime(
    val shop_open_days: String,
    val shop_open_time: String,
    val shop_close_time: String
)

data class ProfileVerifyPaymentRequest(
    val amount: String,
    val days: String,
    val payment_deti: List<String>,
    val user_mobile: String,
    val api_key: String,
    val device_id: String,
    val device_type: String,
    val device_token: String,
    val app_user_id: String,
    val app_version: String,
    val app_user_key: String,
    val lat: Double,
    val lng: Double
)