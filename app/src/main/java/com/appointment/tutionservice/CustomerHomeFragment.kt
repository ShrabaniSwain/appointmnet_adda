package com.appointment.tutionservice

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.appointment.tutionservice.Constant.API_KEY
import com.appointment.tutionservice.Constant.APP_USER_ID
import com.appointment.tutionservice.Constant.APP_USER_KEY
import com.appointment.tutionservice.Constant.APP_VERSION_NAME
import com.appointment.tutionservice.Constant.MOBILE_NO
import com.appointment.tutionservice.databinding.FragmentCustomerHomeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Timer
import java.util.TimerTask

class CustomerHomeFragment : Fragment() {

    private lateinit var binding: FragmentCustomerHomeBinding
    private var timer: Timer? = null
    private var currentPage = 0
    private var currentSmallPage = 0
    private val DELAY_MS: Long = 2000
    private val PERIOD_MS: Long = 2000

    private var lastClickedCard: Pair<ImageView, TextView>? = null
    private var isDoctorsCardExpanded = false
    private var isTeacherCardExpanded = false
    private var isMechanicCardExpanded = false
    private var isPrintingCardExpanded = false
    private var isSecurityCardExpanded = false
    private var isBusinessCardExpanded = false
    private var isArtCraftCardExpanded = false
    private lateinit var banners: List<Banner>
    private lateinit var bannersSmall: List<Banner>
    private lateinit var businessList: List<BusinessService>
    private lateinit var instantService: List<InstantService>
    private val NOTIFICATION_PERMISSION_CODE = 1001

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCustomerHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        askNotificationPermission()
        binding.tvViewAll.setOnClickListener {
            val intent = Intent(requireContext(), ViewAllActivity::class.java)
            startActivity(intent)
        }

        binding.moreTeacherCard.setOnClickListener {
            val intent = Intent(requireContext(), MoreActivity::class.java)
            startActivity(intent)
        }

        binding.tvLocationName.text = Constant.LOCATION_NAME
        fetchBannerData()
        binding.etSearch.setOnClickListener {

//                val intent = Intent(requireContext(), SearchServiceActivity::class.java)
//                startActivity(intent)

            val intent = Intent(requireContext(), SearchProductServiceActivity::class.java)
            startActivity(intent)
        }

        binding.productMenu.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), binding.productMenu)
            popupMenu.menu.add("Service")
            popupMenu.menu.add("Product")
            popupMenu.setOnMenuItemClickListener { menuItem ->
                binding.productMenu.text = menuItem.title
                true
            }
            popupMenu.show()
        }
    }

    private fun askNotificationPermission() {
        // Only request permission if the API level is 33 or higher (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Check if the permission is already granted
            if (ContextCompat.checkSelfPermission(           requireContext(), Manifest.permission.POST_NOTIFICATIONS
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
    private fun fetchBannerData() {
        showProgressBar()
        val call = RetrofitClient.api.getBanner(
            MOBILE_NO,
            API_KEY,
            Utility.getDeviceId(requireContext()),
            Utility.deviceToken,
            Constant.APP_USER_KEY,
            APP_USER_ID.toString(),
            APP_VERSION_NAME.toString()
        )

        call.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call< ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    if (isAdded) {
                    hideProgressBar()
                    binding.serviceBox.visibility = View.VISIBLE
                    binding.tvOurServices.visibility = View.VISIBLE
                    binding.forwardArrow.visibility = View.VISIBLE
                    binding.tvViewAll.visibility = View.VISIBLE
                    banners = response.body()?.data?.banners?.topBanners ?: emptyList()
                    bannersSmall = response.body()?.data?.banners?.smallBanners ?: emptyList()
                    businessList = response.body()?.data?.businessServices ?: emptyList()
                    instantService = response.body()?.data?.instantServices ?: emptyList()

                    response.body()?.data?.featuredServices?.let { featuredServices ->
                        // Iterate over the available featured services and set the views accordingly
                        for ((index, service) in featuredServices.withIndex()) {
                            when (index) {
                                0 -> {
                                    binding.tvTeacher.text = service.service_name
                                    Glide.with(requireContext()).load(service.service_img_path)
                                        .into(binding.ivTeacherLogo)
                                    binding.teacherCardView.setOnClickListener {
                                        Constant.CAT_TYPE = service.service_id
                                        Constant.SERVICE_CAT_TYPE = service.service_type
                                        Constant.JOBTITLE = service.service_name
                                        Constant.SERVICE_CAT_TYPE = service.service_type

                                        handleCardClick(
                                            binding.ivTeacherLogo,
                                            binding.tvTeacher,
                                            R.color.red,
                                            R.color.normal_text_clor,
                                            isTeacherCardExpanded
                                        )
                                        isTeacherCardExpanded = !isTeacherCardExpanded
                                        val intent =
                                            Intent(requireContext(), TeacherActivity::class.java)
                                        startActivity(intent)
                                    }
                                }

                                1 -> {
                                    binding.tvDoctors.text = service.service_name
                                    Glide.with(requireContext()).load(service.service_img_path)
                                        .into(binding.ivMath)

                                    binding.doctorsCard.setOnClickListener {
                                        Constant.CAT_TYPE = service.service_id
                                        Constant.JOBTITLE = service.service_name
                                        Constant.SERVICE_CAT_TYPE = service.service_type

                                        handleCardClick(
                                            binding.ivMath,
                                            binding.tvDoctors,
                                            R.color.red,
                                            R.color.normal_text_clor,
                                            isDoctorsCardExpanded
                                        )
                                        isDoctorsCardExpanded = !isDoctorsCardExpanded
                                        val intent =
                                            Intent(requireContext(), TeacherActivity::class.java)
                                        startActivity(intent)
                                    }

                                }

                                2 -> {
                                    binding.tvMechanic.text = service.service_name
                                    Glide.with(requireContext()).load(service.service_img_path)
                                        .into(binding.ivMechanic)
                                    binding.mechanicCard.setOnClickListener {
                                        Constant.CAT_TYPE = service.service_id
                                        Constant.JOBTITLE = service.service_name
                                        Constant.SERVICE_CAT_TYPE = service.service_type

                                        handleCardClick(
                                            binding.ivMechanic,
                                            binding.tvMechanic,
                                            R.color.red,
                                            R.color.normal_text_clor,
                                            isMechanicCardExpanded
                                        )
                                        isMechanicCardExpanded = !isMechanicCardExpanded
                                        val intent =
                                            Intent(requireContext(), TeacherActivity::class.java)
                                        startActivity(intent)
                                    }
                                }

                                3 -> {
                                    binding.tvPrinting.text = service.service_name
                                    Glide.with(requireContext()).load(service.service_img_path)
                                        .into(binding.ivPrinting)
                                    binding.printingCard.setOnClickListener {
                                        Constant.CAT_TYPE = service.service_id
                                        Constant.JOBTITLE = service.service_name
                                        Constant.SERVICE_CAT_TYPE = service.service_type

                                        handleCardClick(
                                            binding.ivPrinting,
                                            binding.tvPrinting,
                                            R.color.red,
                                            R.color.normal_text_clor,
                                            isPrintingCardExpanded
                                        )
                                        isPrintingCardExpanded = !isPrintingCardExpanded
                                        val intent =
                                            Intent(requireContext(), TeacherActivity::class.java)
                                        startActivity(intent)
                                    }
                                }

                                4 -> {
                                    binding.tvSecurity.text = service.service_name
                                    Glide.with(requireContext()).load(service.service_img_path)
                                        .into(binding.ivSecurityLogo)
                                    binding.securityCardView.setOnClickListener {
                                        Constant.CAT_TYPE = service.service_id
                                        Constant.JOBTITLE = service.service_name
                                        Constant.SERVICE_CAT_TYPE = service.service_type

                                        handleCardClick(
                                            binding.ivSecurityLogo,
                                            binding.tvSecurity,
                                            R.color.red,
                                            R.color.normal_text_clor,
                                            isSecurityCardExpanded
                                        )
                                        isSecurityCardExpanded = !isSecurityCardExpanded
                                        val intent =
                                            Intent(requireContext(), TeacherActivity::class.java)
                                        startActivity(intent)
                                    }
                                }

                                5 -> {
                                    binding.tvBusiness.text = service.service_name
                                    Glide.with(requireContext()).load(service.service_img_path)
                                        .into(binding.ivBusiness)
                                    binding.businessCard.setOnClickListener {
                                        Constant.CAT_TYPE = service.service_id
                                        Constant.JOBTITLE = service.service_name
                                        Constant.SERVICE_CAT_TYPE = service.service_type

                                        handleCardClick(
                                            binding.ivBusiness,
                                            binding.tvBusiness,
                                            R.color.red,
                                            R.color.normal_text_clor,
                                            isBusinessCardExpanded
                                        )
                                        isBusinessCardExpanded = !isBusinessCardExpanded
                                        val intent =
                                            Intent(requireContext(), TeacherActivity::class.java)
                                        startActivity(intent)
                                    }
                                }

                                6 -> {
                                    binding.tvArt.text = service.service_name
                                    Glide.with(requireContext()).load(service.service_img_path)
                                        .into(binding.ivArt)
                                    binding.artCard.setOnClickListener {
                                        Constant.CAT_TYPE = service.service_id
                                        Constant.JOBTITLE = service.service_name
                                        Constant.SERVICE_CAT_TYPE = service.service_type

                                        handleCardClick(
                                            binding.ivArt,
                                            binding.tvArt,
                                            R.color.red,
                                            R.color.normal_text_clor,
                                            isArtCraftCardExpanded
                                        )
                                        isArtCraftCardExpanded = !isArtCraftCardExpanded
                                        val intent =
                                            Intent(requireContext(), TeacherActivity::class.java)
                                        startActivity(intent)
                                    }
                                }
                            }
                        }
                    }

                    val numFeaturedServices = response.body()?.data?.featuredServices?.size ?: 0
                    val viewsToHide = listOf(
                        binding.teacherCardView,
                        binding.doctorsCard,
                        binding.mechanicCard,
                        binding.printingCard,
                        binding.securityCardView,
                        binding.businessCard,
                        binding.artCard
                    )
                    for (i in numFeaturedServices until viewsToHide.size) {
                        viewsToHide[i].visibility = View.GONE
                    }

                    binding.moreTeacherCard.visibility =
                        if (binding.artCard.visibility == View.VISIBLE) View.VISIBLE else View.GONE


                    val guardianBanner = CustomerBanner(requireContext(), banners)
                    binding.rvBanner.layoutManager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                    binding.rvBanner.adapter = guardianBanner
                    guardianBanner.notifyDataSetChanged()

                    val guardianBannerSmall = CustomerBanner(requireContext(), bannersSmall)
                    binding.rvBanner1.layoutManager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                    binding.rvBanner1.adapter = guardianBannerSmall
                    guardianBannerSmall.notifyDataSetChanged()

                    startAutoSlide()

                    val enquiryAdapter = EnquiryAdapter(requireContext(), businessList)
                    binding.rvEnquiryList.adapter = enquiryAdapter
                    binding.rvEnquiryList.layoutManager =
                        GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)

                    val customerServiceAdapter =
                        CustomerServiceAdapter(requireContext(), instantService)
                    binding.rvServiceList.layoutManager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    binding.rvServiceList.adapter = customerServiceAdapter
                }
                    Log.i("TAG", "bannerImage: ${response.body()}")
                } else {
                    hideProgressBar()
                    val errorBody = response.errorBody()
                    Toast.makeText(requireContext(), "Something went wrong" , Toast.LENGTH_SHORT).show()
                    Log.i("TAG", "bannerImage: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                hideProgressBar()
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
                Log.i("TAG", "bannerImage: ${t.message}")

            }
        })
    }

    private fun startAutoSlide() {
        val handler = Handler()
        val update = Runnable {
            if (currentPage == banners.size) {
                currentPage = 0
            }
            if (currentSmallPage == bannersSmall.size) {
                currentSmallPage = 0
            }
            binding.rvBanner.smoothScrollToPosition(currentPage++)
            binding.rvBanner1.smoothScrollToPosition(currentSmallPage++)
        }

        timer = Timer()
        timer!!.schedule(object : TimerTask() {
            override fun run() {
                handler.post(update)
            }
        }, DELAY_MS, PERIOD_MS)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer?.cancel()
    }

    private fun handleCardClick(
        imageView: ImageView,
        textView: TextView,
        selectedColorResId: Int,
        normalColorResId: Int,
        isExpanded: Boolean
    ) {
        if (lastClickedCard?.first == imageView) {
            resetCardView(imageView, textView, normalColorResId)
            lastClickedCard = null
            return
        }

        lastClickedCard?.let { (lastClickedImageView, lastClickedTextView) ->
            resetCardView(lastClickedImageView, lastClickedTextView, normalColorResId)
        }

        val layoutParams = imageView.layoutParams
        val newWidth = (resources.displayMetrics.density * 40).toInt()
        val newHeight = (resources.displayMetrics.density * 40).toInt()
        layoutParams.width = newWidth
        layoutParams.height = newHeight
        imageView.layoutParams = layoutParams

        textView.setTextColor(ContextCompat.getColor(requireContext(), selectedColorResId))

        lastClickedCard = Pair(imageView, textView)
    }


    private fun resetCardView(imageView: ImageView, textView: TextView, normalColorResId: Int) {
        val layoutParams = imageView.layoutParams
        val normalWidth = (resources.displayMetrics.density * 32).toInt()
        val normalHeight = (resources.displayMetrics.density * 32).toInt()
        layoutParams.width = normalWidth
        layoutParams.height = normalHeight
        imageView.layoutParams = layoutParams

        textView.setTextColor(ContextCompat.getColor(requireContext(), normalColorResId))
    }

    private fun getServiceNameByType() {
        showProgressBar()

        val call = RetrofitClient.api.getServiceNameByType(
            MOBILE_NO,
            API_KEY,
            Utility.getDeviceId(requireContext()),
            Utility.deviceToken,
            APP_USER_KEY,
            APP_USER_ID.toString(),
            APP_VERSION_NAME.toString(), "1", "0", "0"
        )
        call.enqueue(object : Callback<ServiceProviderResponse> {
            override fun onResponse(
                call: Call<ServiceProviderResponse>,
                response: Response<ServiceProviderResponse>
            ) {
                if (response.isSuccessful) {
                    hideProgressBar()
                    Log.i("TAG", "getServiceNameByType: ${response.body()}")
                } else {
                    hideProgressBar()
                    Toast.makeText(requireContext(), response.message(), Toast.LENGTH_SHORT).show()
                    Log.i("TAG", "getServiceNameByType: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<ServiceProviderResponse>, t: Throwable) {
                hideProgressBar()
                Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
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
}