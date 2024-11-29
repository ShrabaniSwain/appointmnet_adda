package com.appointment.tutionservice

import android.Manifest
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.MediaController
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appointment.tutionservice.Constant.MESSAGE_AMOUNT
import com.appointment.tutionservice.Constant.PUSH_NOTIFICATION_AMOUNT
import com.appointment.tutionservice.Constant.RAZORPAY_KEY
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.appointment.tutionservice.Constant.VALIDATE_DAYS
import com.appointment.tutionservice.Constant.WALLET_AMOUNT
import com.appointment.tutionservice.Constant.is_membership_package_expire
import com.appointment.tutionservice.databinding.FragmentProviderHomeBinding
import com.appointment.tutionservice.databinding.HeaderLayoutBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProviderHomeFragment : Fragment(), PaymentResultListener {

    private lateinit var binding: FragmentProviderHomeBinding
    private var isLoggedOut = false
    private lateinit var headerBinding: HeaderLayoutBinding
    private var appointment: List<AppointmentProvider> = mutableListOf()
    private var enquiry: List<Enquiry> = mutableListOf()
    private lateinit var providerAppointmnetAdapter: ProviderAppointmnetAdapter
    private lateinit var providerEnquiryItemAdapter: ProviderEnquiryItemAdapter
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val PREFS_NAME = "app_prefs"
    private val FIRST_LAUNCH_KEY = "first_launch"
    private var videoUrl: String? = null // Declare videoUrl globally
    private val NOTIFICATION_PERMISSION_CODE = 1001

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted.
                getCurrentLocation()
            }

            permissions.getOrDefault(ACCESS_COARSE_LOCATION, false) -> {
                // Only approximate location access granted.
                getCurrentLocation()
            }

            else -> {
                // No location access granted.
            }
        }

        askNotificationPermission()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProviderHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (Utility.isInternetAvailable(requireContext())) {
            getServiceProviderDashboard()
            getProviderProfileDetails()
            checkLocationPermissions()

            GlobalScope.launch(Dispatchers.Main) {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.api.allTheData()
                }

                if (response.isSuccessful) {
                    val aboutResponse = response.body()
                    aboutResponse?.let { handleVideoResponse(it.data) }
                } else {
                    Log.i("TAG", "API Call failed with error code: ${response.code()}")
                }
            }
        } else {
            Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show()
        }
        Log.i("TAG", "onViewCreated: " + Utility.getDeviceId(requireContext()))
        headerBinding = HeaderLayoutBinding.bind(binding.navView.getHeaderView(0))
        Glide.with(requireContext())
            .load(Constant.customer_ProfileImage)
            .apply(RequestOptions.placeholderOf(R.drawable.baseline_person_24))
            .into(headerBinding.imageView)

        binding.ivMenu.setOnClickListener {
            getProviderProfileDetails()
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
        binding.notificationBtn.setOnClickListener {
            val intent = Intent(requireContext(), ProviderNotificationActivity::class.java)
            startActivity(intent)
        }

        binding.walletBtn.setOnClickListener {
            val intent = Intent(requireContext(), WalletHistoryActivity::class.java)
            startActivity(intent)
        }

        binding.referralBtn.setOnClickListener {
            val intent = Intent(requireContext(), ReferralActivity::class.java)
            startActivity(intent)
        }

        binding.versionId.text = "Version ${Constant.APP_VERSION_NAME}"

        binding.drawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                val toolbarVisibility = if (slideOffset > 0) View.VISIBLE else View.GONE
                val mainToolbar = if (slideOffset > 0) View.GONE else View.VISIBLE
                showProgressBar()

            }

            override fun onDrawerOpened(drawerView: View) {
                binding.toolbarMenu.visibility = View.VISIBLE
                binding.toolbar.visibility = View.GONE
                hideProgressBar()

            }

            override fun onDrawerClosed(drawerView: View) {
                binding.toolbar.visibility = View.VISIBLE
                binding.toolbarMenu.visibility = View.GONE
                hideProgressBar()
            }
        })

        binding.navView.setNavigationItemSelectedListener { menuItem ->
            for (i in 0 until binding.navView.menu.size()) {
                val item = binding.navView.menu.getItem(i)
                item.isChecked = false
            }
            menuItem.isChecked = true
            binding.drawerLayout.closeDrawers()

            when (menuItem.itemId) {

                R.id.nav_profile -> {
                    val intent = Intent(requireContext(), ProviderProfileActivity::class.java)
                    startActivity(intent)
                }

                R.id.nav_support -> {
                    val intent = Intent(requireContext(), ProviderSupportActivity::class.java)
                    startActivity(intent)
                }

                R.id.nav_privacy -> {
                    val intent = Intent(requireContext(), ProviderPrivacyActivity::class.java)
                    startActivity(intent)
                }

                R.id.nav_todo -> {
                    val intent = Intent(requireContext(), TodoListActivity::class.java)
                    startActivity(intent)
                }

                R.id.nav_terms -> {
                    val intent = Intent(requireContext(), ProviderTermsActivity::class.java)
                    startActivity(intent)
                }

                R.id.nav_terms -> {
                    val intent = Intent(requireContext(), ProviderTermsActivity::class.java)
                    startActivity(intent)
                }

                R.id.nav_tutotial -> {
                    val intent = Intent(requireContext(), VIdeoPlayActivity::class.java)
                    startActivity(intent)
                }

                R.id.nav_log_out -> {
                    showLogoutConfirmationDialog()
                }

            }

            true
        }

        binding.btnUpdatePackage.setOnClickListener {
            val intent = Intent(requireContext(), UpdatePackageActivity::class.java)
            startActivity(intent)
        }

        val bottomHomeFragmentAdapter = BottomHomeFragmentAdapter(requireContext())
        binding.rvService.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvService.adapter = bottomHomeFragmentAdapter
    }

    private fun handleVideoResponse(result: CompanyInfo) {
        if (isAdded) {
            videoUrl = result.video_tutorial[0].files // Store video URL globally

            if (isFirstLaunch()) {
                videoUrl?.let {
                    showVideoDialog(it) // Show dialog on first launch
                }
                setFirstLaunch(false)
            }
        }
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permissions not granted
            return
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    // Use Geocoder to get the location name
                    if (isAdded) {
                        val geocoder = Geocoder(requireContext(), Locale.getDefault())
                        val addresses =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        if (addresses != null) {
                            if (addresses.isNotEmpty()) {
                                val address = addresses[0]
                                val city = address.locality ?: ""
                                val state = address.adminArea ?: ""
                                val postalCode = address.postalCode ?: ""

                                val locationName = "$city, $state, $postalCode"
                                binding.tvLocationName.text = locationName
                            }
                        }
                    }
                }
            }
    }

    private fun askNotificationPermission() {
        // Only request permission if the API level is 33 or higher (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Check if the permission is already granted
            if (ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Request the permission
                ActivityCompat.requestPermissions(
                    requireContext() as Activity,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_CODE
                )
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            // Check if permission was granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
            } else {

                Log.i("TAG", "onRequestPermissionsResult: " + "permission")
            }
        }
    }

    private fun checkLocationPermissions() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                getCurrentLocation()

            }

            shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION) -> {
                // In an educational UI, explain to the user why your app requires this permission.
                // Request the permission.
                locationPermissionRequest.launch(
                    arrayOf(
                        ACCESS_FINE_LOCATION,
                        ACCESS_COARSE_LOCATION
                    )
                )
            }

            else -> {
                // You can directly ask for the permission.
                locationPermissionRequest.launch(
                    arrayOf(
                        ACCESS_FINE_LOCATION,
                        ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    private fun getProviderProfileDetails() {
        val call = RetrofitClient.api.getProviderProfileDetails(
            Constant.MOBILE_NO,
            Constant.API_KEY,
            Utility.getDeviceId(requireContext()),
            Utility.deviceToken,
            Constant.APP_USER_KEY,
            Constant.APP_USER_ID.toString(),
            Constant.APP_VERSION_NAME.toString()
        )
        call.enqueue(object : Callback<UserData> {
            override fun onResponse(call: Call<UserData>, response: Response<UserData>) {
                if (response.isSuccessful) {

//                    if (response.body()?.data?.app_user_data?.is_profile_verified == "2") {
//                        val dialogBuilder = android.app.AlertDialog.Builder(requireContext())
//                        val inflater = LayoutInflater.from(requireContext())
//                            .inflate(R.layout.verification_dialog, null)
//                        dialogBuilder.setView(inflater)
//                        val btnBuy = inflater.findViewById<TextView>(R.id.btnBuy)
//                        val later = inflater.findViewById<TextView>(R.id.tvLater)
//
//                        btnBuy.text =
//                            "Pay Just Rs. " + (response.body()?.data?.app_user_data?.verification_package?.amount
//                                ?: "0")
//                        Constant.BUDGET =
//                            response.body()?.data?.app_user_data?.verification_package?.amount
//                                ?: "0"
//                        VALIDATE_DAYS =
//                            (response.body()?.data?.app_user_data?.verification_package?.valid_days
//                                ?: "0") + "days"
//                        val amount = Constant.BUDGET.replace(Regex("[^\\d]"), "")
//                        val pricePay = Math.round(amount.toFloat() * 100)
//                        val dialog = dialogBuilder.create()
//                        later.setOnClickListener {
//                            dialog.dismiss()
//                        }
//                        btnBuy.setOnClickListener {
//
//                            Checkout.preload(requireContext())
//                            val co = Checkout()
//                            co.setKeyID(RAZORPAY_KEY)
//                            val obj = JSONObject()
//                            try {
//                                obj.put("description", "Test payment")
//                                obj.put("theme.color", "")
//                                obj.put("currency", "INR")
//                                obj.put("amount", pricePay)
//                                obj.put("name", Constant.Provider_Name)
//                                obj.put("prefill.contact", Constant.MOBILE_NO)
//                                obj.put("prefill.email", Constant.EMAIL)
//                                co.open(requireActivity(), obj)
//                                Log.i("TAG", "PAYMENTResponse: " + obj)
//                            } catch (e: JSONException) {
//                                e.printStackTrace()
//                                Log.i("TAG", "PAYMENTResponse: " + e.message)
//                            }
//                            dialog.dismiss()
//                        }
//
//                        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//                        dialog.show()
//                    }

//
                    val referralCode =
                        response.body()?.data?.app_user_data?.referral_id ?: "Not Available"
                    binding.tvReferralId.text = referralCode
                    WALLET_AMOUNT = response.body()?.data?.app_user_data?.total_wallet_amount ?: "0"
                    PUSH_NOTIFICATION_AMOUNT =
                        response.body()?.data?.app_user_data?.charge_push_notification ?: "0"
                    MESSAGE_AMOUNT =
                        response.body()?.data?.app_user_data?.charge_whatsapp_sms ?: "0"

                    binding.btnShare.setOnClickListener {
                        if (isAdded){
                        val shareMessage = """
        Hey, check out this amazing app!
        Use my referral code $referralCode to get started and enjoy exclusive benefits.
        
        Download the app now: "https://play.google.com/store/apps/details?id=com.appointment.tutionservice"
    """.trimIndent()
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, shareMessage)
                        }

                        startActivity(Intent.createChooser(shareIntent, "Share via"))
                    }
                    }

                    Constant.customer_ProfileImage =
                        response.body()?.data?.app_user_data?.profile_image ?: ""

                    if (isAdded) {
                        Glide.with(requireContext())
                            .load(response.body()?.data?.app_user_data?.profile_image)
                            .apply(RequestOptions.placeholderOf(R.drawable.baseline_person_24))
                            .into(headerBinding.imageView)
                        headerBinding.tvPhone.text = Editable.Factory.getInstance().newEditable(
                            response.body()?.data?.app_user_data?.user_mobile
                                ?: ""
                        )
                    }
                    if (response.body()?.data?.app_user_data?.is_profile_verified == "1") {
                        headerBinding.tvProfileId.visibility = View.VISIBLE
                    } else {
                        headerBinding.tvProfileIdNot.visibility = View.VISIBLE
//                        headerBinding.tvProfileIdNot.setOnClickListener {
//                            val dialogBuilder = android.app.AlertDialog.Builder(requireContext())
//                            val inflater = LayoutInflater.from(requireContext()).inflate(R.layout.verification_dialog, null)
//                            dialogBuilder.setView(inflater)
//                            val btnBuy = inflater.findViewById<TextView>(R.id.btnBuy)
//                            val later = inflater.findViewById<TextView>(R.id.tvLater)
//
//                            btnBuy.text = "Pay Just Rs. " + (response.body()?.data?.app_user_data?.verification_package?.amount ?: "0")
//                            Constant.BUDGET = response.body()?.data?.app_user_data?.verification_package?.amount ?: "0"
//                            VALIDATE_DAYS = (response.body()?.data?.app_user_data?.verification_package?.valid_days ?: "0") + "days"
//                            val amount = Constant.BUDGET.replace(Regex("[^\\d]"), "")
//                            val pricePay = Math.round(amount.toFloat() * 100)
//                            val dialog = dialogBuilder.create()
//                            later.setOnClickListener {
//                                dialog.dismiss()
//                            }
//                            btnBuy.setOnClickListener {
//
//                                Checkout.preload(requireContext())
//                                val co = Checkout()
//                                co.setKeyID(RAZORPAY_KEY)
//                                val obj = JSONObject()
//                                try {
//                                    obj.put("description", "Test payment")
//                                    obj.put("theme.color", "")
//                                    obj.put("currency", "INR")
//                                    obj.put("amount", pricePay)
//                                    obj.put("name", Constant.Provider_Name)
//                                    obj.put("prefill.contact", Constant.MOBILE_NO)
//                                    obj.put("prefill.email", Constant.EMAIL)
//                                    co.open(requireActivity(), obj)
//                                    Log.i("TAG", "PAYMENTResponse: " + obj)
//                                } catch (e: JSONException) {
//                                    e.printStackTrace()
//                                    Log.i("TAG", "PAYMENTResponse: " + e.message)
//                                }
//                                dialog.dismiss()
//                            }
//
//                            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//                            dialog.show()
//                        }
                    }
                    headerBinding.tvEmail.text = Editable.Factory.getInstance().newEditable(
                        response.body()?.data?.app_user_data?.user_email
                            ?: ""
                    )
                    headerBinding.tvName.text = Editable.Factory.getInstance().newEditable(
                        response.body()?.data?.app_user_data?.user_profile_name
                            ?: ""
                    )
                    Constant.Provider_Name = response.body()?.data?.app_user_data?.user_profile_name
                        ?: ""
                    Constant.EMAIL = response.body()?.data?.app_user_data?.user_email
                        ?: ""
                    Log.i("TAG", "responseBody: ${response.body()}")
                } else {
                    Toast.makeText(requireContext(), response.message(), Toast.LENGTH_SHORT)
                        .show()
                    Log.i("TAG", "responseBody: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<UserData>, t: Throwable) {
                Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                Log.i("TAG", "responseBody: Something went wrong.")

            }
        })
    }

    private fun clearSessionData() {
        val sharedPrefHelper = SharedPreferenceHelper(requireContext())
        sharedPrefHelper.clearSession()
    }

    private fun navigateToLoginScreen() {
        isLoggedOut = true
        val intent = Intent(requireContext(), LoginMobileNoHome::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        if (isLoggedOut) {
            navigateToLoginScreen()
        }
    }

    private fun showLogoutConfirmationDialog() {
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.logout_dialog_box, null)
        val dialogOk = dialogView.findViewById<TextView>(R.id.btnOk)
        val dialogCancel = dialogView.findViewById<TextView>(R.id.btnCancel)

        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialogOk.setOnClickListener {
            clearSessionData()
            navigateToLoginScreen()
            alertDialog.dismiss()

            Toast.makeText(requireContext(), "Logged out successfully!", Toast.LENGTH_SHORT).show()
        }

        dialogCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun getServiceProviderDashboard() {
        showProgressBar()

        val call = RetrofitClient.api.getServiceProviderDashboard(
            Constant.MOBILE_NO,
            Constant.API_KEY,
            Utility.getDeviceId(requireContext()),
            Constant.APP_USER_KEY,
            Constant.APP_USER_ID.toString(),
            Constant.APP_VERSION_NAME.toString(),
            "0",
            "0",
            "undefined",
            "undefined",
            "undefined",
            "736101",
            "undefined"
        )
        call.enqueue(object : Callback<DashboardProviderResponse> {
            override fun onResponse(
                call: Call<DashboardProviderResponse>,
                response: Response<DashboardProviderResponse>
            ) {
                Log.i("TAG", "onResponse: " + response)
                if (response.isSuccessful) {
                    hideProgressBar()

                    binding.tvActiveEnquiry.text = response.body()?.data?.active_enquiry.toString()
                    binding.notificationCount.text = response.body()?.data?.total_notification ?: "0"
                    is_membership_package_expire =
                        response.body()?.data?.isMembershipPackageExpire == true

                    val originalDateString = response.body()?.data?.membershipPackageExpiryDate
                    if (!originalDateString.isNullOrEmpty()) {
                        val parsedDate = try {
                            SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(
                                originalDateString
                            )
                        } catch (e: ParseException) {
                            e.printStackTrace()
                            null
                        }

                        val formattedDate = parsedDate?.let {
                            SimpleDateFormat(
                                "dd MMM yyyy",
                                Locale.getDefault()
                            ).format(it)
                        }
                        binding.tvExpireDate.text = "Expires On: $formattedDate"

//                        if (formattedDate.isNullOrEmpty()){
//                        parsedDate?.let { date ->
//                            val currentDate = Date()
//                            if (date.before(currentDate)) {
//                                val intent = Intent(requireContext(), PackageActivity::class.java)
//                                Constant.CARD_TYPE = "Home"
//                                startActivity(intent)
//                                requireActivity().finish()
//                            }
//                        }
//                        }
                    } else {
                        binding.tvExpireDate.text = "No active subscription"
//                        val intent = Intent(requireContext(), PackageActivity::class.java)
//                        Constant.CARD_TYPE = "Home"
//                        startActivity(intent)
//                        requireActivity().finish()
                    }
                    val statusNumber = response.body()?.data?.membershipPackage?.status
                    val status = when (statusNumber) {
                        "1" -> "Active"
                        "2" -> "Confirmed"
                        "3" -> "On the way for service"
                        "4" -> "Completed"
                        "5" -> "Cancelled"
                        "6" -> "Expired"
                        "7" -> "Pending"
                        "8" -> "In Progress"
                        else -> "Active"
                    }

                    binding.btnViewPackage.setOnClickListener {
                        val dialog = Dialog(requireContext())
                        dialog.setContentView(R.layout.view_package_dialog)

                        val tvDialogTitle = dialog.findViewById<TextView>(R.id.tvDialogTitle)
                        val tvDescription = dialog.findViewById<TextView>(R.id.tvDescription)
                        val tvExpireDate = dialog.findViewById<TextView>(R.id.tvExpireDate)
                        val btnOk = dialog.findViewById<TextView>(R.id.btnOk)

                        val packages = response.body()?.data?.membershipPackage?.packageName
                        if (packages.isNullOrEmpty()) {
                            tvDialogTitle.text = "Package"
                            tvDescription.text = "Package Price 0.0"
                            tvExpireDate.text = "Validity Days 0"
                        } else {
                            tvDialogTitle.text =
                                response.body()?.data?.membershipPackage?.packageName
                            tvDescription.text =
                                "Package Price ${response.body()?.data?.membershipPackage?.packagePrice}"
                            tvExpireDate.text =
                                "Validity Days ${response.body()?.data?.membershipPackage?.validityDays}"
                        }

                        btnOk.setOnClickListener {
                            dialog.dismiss()
                        }
                        dialog.show()
                    }

                    binding.tvActive.text = status
                    binding.tvActiveAppointmentCount.text =
                        response.body()?.data?.active_appointment.toString()
                    binding.tvCompletedAppointments.text =
                        response.body()?.data?.completed_appointments.toString()
                    binding.tvCountCustomer.text = response.body()?.data?.total_customer.toString()
                    binding.tvCountBusiness.text = response.body()?.data?.total_bids.toString()
                    binding.tvReceivedPayments.text =
                        response.body()?.data?.reffered.toString()

                    appointment = response.body()?.data?.appoinsments ?: emptyList()
                    enquiry = response.body()?.data?.enquiry ?: emptyList()

                    if (isAdded) {
                        providerAppointmnetAdapter =
                            ProviderAppointmnetAdapter(requireContext(), appointment)
                        binding.rvAppointment.layoutManager = LinearLayoutManager(
                            requireContext(),
                            LinearLayoutManager.HORIZONTAL,
                            false
                        )
                        binding.rvAppointment.adapter = providerAppointmnetAdapter

                        providerEnquiryItemAdapter =
                            ProviderEnquiryItemAdapter(requireContext(), enquiry)
                        binding.rvEnquiry.layoutManager = LinearLayoutManager(
                            requireContext(),
                            LinearLayoutManager.HORIZONTAL,
                            false
                        )
                        binding.rvEnquiry.adapter = providerEnquiryItemAdapter
                    }

                    Log.i("TAG", "getServiceNameByType: ${response.body()}")
                } else {
                    hideProgressBar()
                    Toast.makeText(requireContext(), response.message(), Toast.LENGTH_SHORT).show()
                    Log.i("TAG", "getServiceNameByType: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<DashboardProviderResponse>, t: Throwable) {
                hideProgressBar()
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
                Log.i("TAG", "getServiceNameByType: ${t.message}")

            }
        })
    }

    private fun showProgressBar() {
        if (isAdded) {
            binding.progressBar.visibility = View.VISIBLE
            requireActivity().window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
        }
    }

    private fun hideProgressBar() {
        if (isAdded) {
            binding.progressBar.visibility = View.GONE
            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }

    override fun onPaymentSuccess(p0: String?) {
        try {
            Toast.makeText(requireContext(), "Payment is successful", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("TAG", "Error in onPaymentSuccess: " + e.message)
        }
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        try {
            Log.i("TAG", "PAYMENTResponse: " + p0)
            Toast.makeText(requireContext(), "Payment Failed due to error", Toast.LENGTH_SHORT)
                .show()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("TAG", "Error in onPaymentError: " + e.message)
        }
    }

    private fun showVideoDialog(videoUrl: String) {
        val dialogBuilder = android.app.AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context).inflate(R.layout.video_play_dialog, null)
        dialogBuilder.setView(inflater)

        val videoView = inflater.findViewById<VideoView>(R.id.videoView)
        val ivClose = inflater.findViewById<AppCompatImageView>(R.id.ivClose)
        val uri = Uri.parse(videoUrl)
        videoView.setVideoURI(uri)

        val mediaController = MediaController(context)
        mediaController.setAnchorView(videoView)
        videoView.setMediaController(mediaController)
        videoView.start()

        val dialog = dialogBuilder.create()
        ivClose.setOnClickListener {
            dialog.dismiss()
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    private fun isFirstLaunch(): Boolean {
        val sharedPref = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPref.getBoolean(FIRST_LAUNCH_KEY, true)
    }

    private fun setFirstLaunch(isFirstLaunch: Boolean) {
        val sharedPref = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean(FIRST_LAUNCH_KEY, isFirstLaunch)
        editor.apply()
    }

}