package com.appointment.tutionservice

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.appointment.tutionservice.Constant.CUSTOMER_Name
import com.appointment.tutionservice.databinding.ActivityMainBinding
import com.appointment.tutionservice.databinding.HeaderLayoutBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CustomerMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isLoggedOut = false
    private lateinit var headerBinding: HeaderLayoutBinding
    private val homeFragment: Fragment = CustomerHomeFragment()
    private val requestFragment: Fragment = CustomerRequestsFragment()
    private val notificationFragment: Fragment = CustomerNotificationFragment()
    private val messagesFragment: Fragment = CustomerMessagesFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showProgressBar()
        getCustomerProfileDetails()

//        val intent = Intent(this, ApiService::class.java)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            startForegroundService(intent)
//        } else {
//            startService(intent)
//        }

        headerBinding = HeaderLayoutBinding.bind(binding.navView.getHeaderView(0))
        Glide.with(applicationContext)
            .load(Constant.customer_ProfileImage)
            .apply(RequestOptions.placeholderOf(R.drawable.baseline_person_24))
            .into(headerBinding.imageView)

        binding.bottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.actionHome -> replaceFragment(homeFragment)
                R.id.actionNotification -> replaceFragment(notificationFragment)
                R.id.actionRequests -> replaceFragment(requestFragment)
                R.id.actionMessage -> replaceFragment(messagesFragment)
                R.id.actionMore ->
                    openNavView()
            }
            true
        }


        binding.drawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                val toolbarVisibility = if (slideOffset > 0) View.VISIBLE else View.GONE
                binding.toolbar.visibility = toolbarVisibility

            }

            override fun onDrawerOpened(drawerView: View) {

            }

            override fun onDrawerClosed(drawerView: View) {

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
                    val intent = Intent(this@CustomerMainActivity, CustomerProfileActivity::class.java)
                    startActivity(intent)
                }

                R.id.nav_support -> {
                    val intent = Intent(this@CustomerMainActivity, CustomerSupportActivity::class.java)
                    startActivity(intent)
                }

                R.id.nav_privacy -> {
                    val intent = Intent(this@CustomerMainActivity, CustomerPrivacyPolicy::class.java)
                    startActivity(intent)
                }

                R.id.nav_terms -> {
                    val intent = Intent(this@CustomerMainActivity, CustomerTermsActivity::class.java)
                    startActivity(intent)
                }

                R.id.nav_log_out ->{
                    showLogoutConfirmationDialog()
                }

            }

            true
        }

    }

    private fun openNavView(){
        binding.drawerLayout.openDrawer(GravityCompat.START)
        getCustomerProfileDetails()
    }

    private fun replaceFragment(fragment: Fragment) {
        hideProgressBar()
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }

    private fun clearSessionData() {
        val sharedPrefHelper = SharedPreferenceHelper(this)
        sharedPrefHelper.clearSession()
    }

    private fun navigateToLoginScreen() {
        isLoggedOut = true
        val intent = Intent(this, LoginMobileNoHome::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    override fun onResume() {
        super.onResume()
        if (isLoggedOut) {
            navigateToLoginScreen()
        }
    }

    private fun showLogoutConfirmationDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.logout_dialog_box, null)
        val dialogOk = dialogView.findViewById<TextView>(R.id.btnOk)
        val dialogCancel = dialogView.findViewById<TextView>(R.id.btnCancel)

        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialogOk.setOnClickListener {
            clearSessionData()
            navigateToLoginScreen()
            alertDialog.dismiss()

            Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_SHORT).show()
        }

        dialogCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun getCustomerProfileDetails() {
        showProgressBar()
        val call = RetrofitClient.api.getCustomerProfileDetails(
            Constant.MOBILE_NO,
            Constant.API_KEY,
            Utility.getDeviceId(applicationContext),
            Utility.deviceToken,
            Constant.APP_USER_KEY,
            Constant.APP_USER_ID.toString(),
            Constant.APP_VERSION_NAME.toString()
        )
        call.enqueue(object : Callback<CustomerProfileResponse> {
            override fun onResponse(call: Call<CustomerProfileResponse>, response: Response<CustomerProfileResponse>) {
                if (response.isSuccessful) {
                    binding.versionId.text = "Version ${Constant.APP_VERSION_NAME}"
                    Constant.customer_ProfileImage = response.body()?.data?.appUserData?.profileImage ?: ""
                    Constant.APP_ADDRESS_ID = response.body()?.data?.appUserData?.appAddressId ?: ""
                    Constant.LOCATION_NAME =
                        (response.body()?.data?.appUserData?.cityName + "," + response.body()?.data?.appUserData?.stateName + "," + response.body()?.data?.appUserData?.pinCode) ?: ""
                    Glide.with(applicationContext)
                        .load(Constant.customer_ProfileImage)
                        .apply(RequestOptions.placeholderOf(R.drawable.baseline_person_24))
                        .into(headerBinding.imageView)
                    headerBinding.tvPhone.text = Editable.Factory.getInstance().newEditable(response.body()?.data?.appUserData?.userMobile
                        ?: ""
                    )
                    headerBinding.tvEmail.text = Editable.Factory.getInstance().newEditable(response.body()?.data?.appUserData?.userEmail
                        ?: ""
                    )
                    headerBinding.tvName.text = Editable.Factory.getInstance().newEditable(response.body()?.data?.appUserData?.userProfileName
                        ?: ""
                    )
                    CUSTOMER_Name = response.body()?.data?.appUserData?.userProfileName
                        ?: ""
                    headerBinding.tvProfileId.text = "ID: ${Constant.APP_USER_ID}"
                    Log.i("TAG", "responseBody: ${response.body()}")
                    replaceFragment(homeFragment)
                } else {
                    hideProgressBar()
                    Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT).show()
                    Log.i("TAG", "responseBody: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<CustomerProfileResponse>, t: Throwable) {
                hideProgressBar()
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
                Log.i("TAG", "responseBody: ${t.message}")

            }
        })
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

}