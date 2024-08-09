package com.appointment.tutionservice

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.appointment.tutionservice.Constant.API_KEY
import com.appointment.tutionservice.Constant.APP_USER_ID
import com.appointment.tutionservice.Constant.APP_USER_KEY
import com.appointment.tutionservice.Constant.APP_VERSION_NAME
import com.appointment.tutionservice.Constant.MOBILE_NO
import com.appointment.tutionservice.databinding.ActivityProfileCurrentLocationBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class ProfileCurrentLocationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileCurrentLocationBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: Location? = null
    private var appAddressId = "0"
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileCurrentLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        binding.tvMyLocation.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            if (checkLocationPermission()) {
                getCurrentLocation()
                binding.progressBar.visibility = View.GONE
            } else {
                requestLocationPermission()
                binding.progressBar.visibility = View.GONE
            }
        }
        appUserAddressInfo()
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.setting.setOnClickListener {
            val intent = Intent(applicationContext, SettingsActivity::class.java)
            startActivity(intent)
        }
        binding.scanned.setOnClickListener {
            val intent = Intent(applicationContext, ProfileScannerActivity::class.java)
            startActivity(intent)
        }
        binding.profile.setOnClickListener {
            val intent = Intent(applicationContext, ProviderProfileActivity::class.java)
            startActivity(intent)
        }

        binding.btnNext.setOnClickListener {
            val houseNo = binding.etNameSelect.text.toString()
            val streetName = binding.etMobileNo.text.toString()
            val locality = binding.etEmailIDSelect.text.toString()
            val pincode = binding.etGenderSelect.text.toString()
            val city = binding.etCitySelect.text.toString()
            val state = binding.etStateSelect.text.toString()
            val landmark = binding.etAddressSelect.text.toString()

            val providerAddressData = ProviderAddressData(
                appAddressId,
                locality,
                houseNo,
                streetName,
                pincode,
                city,
                state,
                landmark,
                MOBILE_NO,
                API_KEY,
                Utility.getDeviceId(applicationContext),
                Utility.deviceType,
                Utility.deviceToken,
                APP_USER_ID.toString(),
                APP_VERSION_NAME.toString(),
                APP_USER_KEY,
                0.0,
                0.0
            )

            if (locality.isEmpty() || pincode.isEmpty() || city.isEmpty() || state.isEmpty() || landmark.isEmpty()
            ) {
                Toast.makeText(applicationContext, "Please fill in all fields", Toast.LENGTH_SHORT)
                    .show()
            } else {
                binding.progressBar.visibility = View.VISIBLE
                appUserAddressUpdate(providerAddressData)
                Log.i("TAG", "providerAddressData: $providerAddressData")
            }
        }
    }

    private fun appUserAddressUpdate(providerAddressData: ProviderAddressData) {

        val call = RetrofitClient.api.appUserAddressUpdate(providerAddressData)
        call.enqueue(object : Callback<ProviderAddressResponse> {
            override fun onResponse(call: Call<ProviderAddressResponse>, response: Response<ProviderAddressResponse>) {
                if (response.isSuccessful) {
                    val updateProfileResponse = response.body()
                    Log.i("TAG", "onResponse: " + response.body())
                    updateProfileResponse?.let {
                        if (it.status == 1) {
                            binding.progressBar.visibility = View.GONE
                            appAddressId = response.body()!!.data.app_user_address
                            val intent = Intent(applicationContext, ProfileScannerActivity::class.java)
                            startActivity(intent)
                        } else {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(
                                applicationContext,
                                "Something went wrong",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    Toast.makeText(
                        applicationContext,
                        response.body()?.msg,
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.progressBar.visibility = View.GONE
                    Log.e("API", "API call failed with code ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ProviderAddressResponse>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Log.e("API", "API call failed with exception: ${t.message}")
                Toast.makeText(
                    applicationContext,
                    t.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun checkLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
           ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            } else {
                Toast.makeText(
                    this,
                    "Location permission denied. Cannot fetch current location.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
               ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
               ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission()
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    currentLocation = location
                    setAddressFieldsWithCurrentLocation()
                } else {
                    Toast.makeText(
                        this,
                        "Unable to fetch current location",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Error occurred: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }


    private fun setAddressFieldsWithCurrentLocation() {
        currentLocation?.let { location ->
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    val address = addresses[0]
                    binding.etNameSelect.setText(address.thoroughfare ?: "")
                    binding.etMobileNo.setText(address.subThoroughfare ?: "")
                    binding.etEmailIDSelect.setText(address.subLocality ?: "")
                    binding.etGenderSelect.setText(address.postalCode ?: "")
                    binding.etCitySelect.setText(address.locality ?: "")
                    binding.etStateSelect.setText(address.adminArea ?: "")
                    binding.etAddressSelect.setText(address.featureName ?: "")
                } else {
                    Toast.makeText(
                        this,
                        "No address found for current location",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun appUserAddressInfo() {
        binding.progressBar.visibility = View.VISIBLE
        val call = RetrofitClient.api.appUserAddressInfo(
            MOBILE_NO,
            API_KEY,
            Utility.getDeviceId(applicationContext),
            Utility.deviceToken,
            APP_USER_KEY,
            APP_USER_ID.toString(),
            APP_VERSION_NAME.toString()
        )
        call.enqueue(object : Callback<GetLocationResponse> {
            override fun onResponse(call: Call<GetLocationResponse>, response: Response<GetLocationResponse>) {
                if (response.isSuccessful) {
                    appAddressId = response.body()?.data?.appUserAddresses?.get(0)?.appAddressId.toString()
                    binding.progressBar.visibility = View.GONE

                    binding.etNameSelect.text = Editable.Factory.getInstance().newEditable(
                        response.body()?.data?.appUserAddresses?.get(0)?.buildingNo
                            ?: ""
                    )
                    binding.etMobileNo.text = Editable.Factory.getInstance().newEditable(
                        response.body()?.data?.appUserAddresses?.get(0)?.streetName
                            ?: ""
                    )
                    binding.etEmailIDSelect.text = Editable.Factory.getInstance().newEditable(
                        response.body()?.data?.appUserAddresses?.get(0)?.locality
                            ?: ""
                    )
                    binding.etGenderSelect.text = Editable.Factory.getInstance().newEditable(
                        response.body()?.data?.appUserAddresses?.get(0)?.pinCode
                            ?: ""
                    )
                    binding.etCitySelect.text = Editable.Factory.getInstance().newEditable(
                        response.body()?.data?.appUserAddresses?.get(0)?.city
                            ?: ""
                    )
                    binding.etStateSelect.text = Editable.Factory.getInstance().newEditable(
                        response.body()?.data?.appUserAddresses?.get(0)?.state
                            ?: ""
                    )
                    binding.etAddressSelect.text = Editable.Factory.getInstance().newEditable(
                        response.body()?.data?.appUserAddresses?.get(0)?.landmark
                            ?: ""
                    )

                    Log.i("TAG", "Sorry, No addresses found.")
                } else {
                    binding.progressBar.visibility = View.GONE
                    val errorBody = response.errorBody()
                    Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT)
                        .show()
                    Log.i("TAG", "Sorry, No addresses found.")
                }
            }

            override fun onFailure(call: Call<GetLocationResponse>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(applicationContext, "Sorry, No addresses found.", Toast.LENGTH_SHORT).show()
                Log.i("TAG", "Sorry, No addresses found.")

            }
        })
    }

}