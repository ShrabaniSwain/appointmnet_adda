package com.appointment.tutionservice

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.appointment.tutionservice.databinding.FragmentProviderRequestBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class ProviderRequestFragment : Fragment() {
    private lateinit var binding: FragmentProviderRequestBinding
    private val calendar: Calendar = Calendar.getInstance()
    private var appointment: List<Appointment> = mutableListOf()
    private var enquiry: List<CustomerEnquiry> = mutableListOf()
    var search = "Enquiry"

    private lateinit var requestAppointmentAdapter: ProviderRequestAppointmentAdapter
    private lateinit var requestEnquiryAdapter: ProviderRequestEnquiryAdapter

    private var filteredAppointment: List<Appointment> = mutableListOf()
    private var filteredEnquiry: List<CustomerEnquiry> = mutableListOf()
    private var startDate: Date? = null
    private var endDate: Date? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProviderRequestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (Utility.isInternetAvailable(requireContext())) {
            showProgressBar()
            getCustomerJobListing()
        }
        else {
            Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show()
        }
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val searchText = p0.toString()

                if (search == "Enquiry") {
                    val filteredList = filteredEnquiry.filter { marketDetail ->
                        marketDetail.service_name.contains(searchText, ignoreCase = true)
                    }
                    requestEnquiryAdapter.updateListEnquiry(filteredList)
                }
                if (search == "Appointment") {
                    val filteredList = filteredAppointment.filter { marketDetail ->
                        marketDetail.service_name.contains(searchText, ignoreCase = true)
                    }
                    requestAppointmentAdapter.updateListAppointmnet(filteredList)
                }

            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        requestAppointmentAdapter = ProviderRequestAppointmentAdapter(
            requireContext(),
            filteredAppointment
        )
        binding.rvServiceAppointment.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvServiceAppointment.adapter = requestAppointmentAdapter


        requestEnquiryAdapter = ProviderRequestEnquiryAdapter(requireContext(), filteredEnquiry)
        binding.rvService.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvService.adapter = requestEnquiryAdapter
        binding.rvServiceAppointment.visibility = View.GONE
        binding.rvService.visibility = View.VISIBLE

        updateButtonColors(true)

        binding.btnAppointment.setOnClickListener {
            search = "Appointment"
            binding.rvServiceAppointment.visibility = View.VISIBLE
            binding.rvService.visibility = View.GONE
            requestAppointmentAdapter = ProviderRequestAppointmentAdapter(
                requireContext(),
                filteredAppointment
            )
            binding.rvServiceAppointment.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            binding.rvServiceAppointment.adapter = requestAppointmentAdapter

            updateButtonColors(false)
            if (filteredAppointment.isEmpty()){
                binding.tvNoData.visibility = View.VISIBLE
            }else{
                binding.tvNoData.visibility = View.GONE

            }
        }

        binding.btnEnquiry.setOnClickListener {
            search = "Enquiry"
            binding.rvServiceAppointment.visibility = View.GONE
            binding.rvService.visibility = View.VISIBLE
            binding.rvService.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            binding.rvService.adapter = requestEnquiryAdapter

            updateButtonColors(true)

            if (filteredEnquiry.isEmpty()){
                binding.tvNoData.visibility = View.VISIBLE
            }else{
                binding.tvNoData.visibility = View.GONE

            }
        }

        binding.tvStartCalender.setOnClickListener {
            openStartDatePickerDialog()
        }
        binding.tvEndCalender.setOnClickListener {
            openEndDatePickerDialog()
        }

    }

    private fun updateButtonColors(isEnquirySelected: Boolean) {
        if (isEnquirySelected) {
            binding.btnEnquiry.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.btnAppointment.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            binding.btnEnquiry.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.red))
            binding.btnAppointment.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.grey))
        } else {
            binding.btnEnquiry.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            binding.btnAppointment.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            binding.btnEnquiry.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.grey))
            binding.btnAppointment.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.red))
        }
    }

    private fun updateStartDateField() {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        binding.tvStartCalender.text = sdf.format(calendar.time)
        startDate = calendar.time
        filterLists()
    }
    private fun updateENdDateField() {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        binding.tvEndCalender.text = sdf.format(calendar.time)
        endDate = calendar.time
        filterLists()
    }

    private fun filterLists(searchText: String = "") {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        filteredEnquiry = enquiry.filter {
            it.service_name?.contains(searchText, ignoreCase = true) == true &&
                    isWithinDateRange(sdf.parse(it.doc))

        }
        filteredAppointment = appointment.filter {
            it.service_name.contains(searchText, ignoreCase = true) &&
                    isWithinDateRange(sdf.parse(it.doc))
        }
        requestEnquiryAdapter.updateListEnquiry(filteredEnquiry)
        requestAppointmentAdapter.updateListAppointmnet(filteredAppointment)
    }


    private fun isWithinDateRange(date: Date?): Boolean {
        if (date == null) return false
        val start = startDate?.let {
            Calendar.getInstance().apply {
                time = it
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time
        } ?: Date(0) // Default to epoch if not set

        val end = endDate?.let {
            Calendar.getInstance().apply {
                time = it
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }.time
        } ?: Date(Long.MAX_VALUE) // Default to far future if not set

        return date in start..end
    }

    private fun openStartDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(
            requireContext(),R.style.DialogTheme,
            { _, year, monthOfYear, dayOfMonth ->
                calendar.set(year, monthOfYear, dayOfMonth)
                updateStartDateField()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun openEndDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(
            requireContext(),R.style.DialogTheme,
            { _, year, monthOfYear, dayOfMonth ->
                calendar.set(year, monthOfYear, dayOfMonth)
                updateENdDateField()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
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

    private fun getCustomerJobListing() {
        showProgressBar()

        val call = RetrofitClient.api.getServiceProviderJobListing(
            Constant.MOBILE_NO,
            Constant.API_KEY,
            Utility.getDeviceId(requireContext()),
            Utility.deviceToken,
            Constant.APP_USER_KEY,
            Constant.APP_USER_ID.toString(),
            Constant.APP_VERSION_NAME.toString(), "1", "0","0","0"
        )
        Log.d("AppValues", "MOBILE_NO: ${Constant.MOBILE_NO}, " +
                "API_KEY: ${Constant.API_KEY}, " +
                "Device ID: ${Utility.getDeviceId(requireContext())}, " +
                "Device Token: ${Utility.deviceToken}, " +
                "APP_USER_KEY: ${Constant.APP_USER_KEY}, " +
                "APP_USER_ID: ${Constant.APP_USER_ID.toString()}, " +
                "APP_VERSION_NAME: ${Constant.APP_VERSION_NAME.toString()}")

        call.enqueue(object : Callback<BookingApiResponse> {
            override fun onResponse(call: Call<BookingApiResponse>, response: Response<BookingApiResponse>) {
                Log.i("TAG", "onResponse: " + call + "response: " + response)
                if (response.isSuccessful) {
                    if (response.body()?.status == 1) {
                        if (isAdded) {
                            hideProgressBar()
                            val questionnaireResponse = response.body()
                            appointment =
                                questionnaireResponse?.data?.appointment_list ?: emptyList()
                            enquiry =
                                questionnaireResponse?.data?.customer_enquiry_list ?: emptyList()
                            filterLists()
                            if (filteredEnquiry.isEmpty()) {
                                binding.tvNoData.visibility = View.VISIBLE
                            } else {
                                binding.tvNoData.visibility = View.GONE

                            }
                            requestAppointmentAdapter = ProviderRequestAppointmentAdapter(
                                requireContext(),
                                filteredAppointment
                            )
                            binding.rvServiceAppointment.layoutManager =
                                LinearLayoutManager(
                                    requireContext(),
                                    LinearLayoutManager.VERTICAL,
                                    false
                                )
                            binding.rvServiceAppointment.adapter = requestAppointmentAdapter


                            requestEnquiryAdapter =
                                ProviderRequestEnquiryAdapter(requireContext(), filteredEnquiry)
                            binding.rvService.layoutManager =
                                LinearLayoutManager(
                                    requireContext(),
                                    LinearLayoutManager.VERTICAL,
                                    false
                                )
                            binding.rvService.adapter = requestEnquiryAdapter
                        }

                        Log.i("TAG", "getServiceNameByType: ${response.body()}")
                    }
                } else {
                    hideProgressBar()
                    Toast.makeText(requireContext(), response.message(), Toast.LENGTH_SHORT).show()
                    Log.i("TAG", "getServiceNameByType: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<BookingApiResponse>, t: Throwable) {
                hideProgressBar()
                Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                Log.i("TAG", "getServiceNameByType: ${t.message}")

            }
        })
    }
}

