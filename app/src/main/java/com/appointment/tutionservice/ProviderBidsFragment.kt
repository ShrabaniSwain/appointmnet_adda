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
import androidx.recyclerview.widget.LinearLayoutManager
import com.appointment.tutionservice.databinding.FragmentProviderBidsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class ProviderBidsFragment : Fragment() {

    private lateinit var binding: FragmentProviderBidsBinding
    private var enquiry: List<JobBidListing> = mutableListOf()
    private lateinit var bidsAdapter: BidsAdapter
    private val calendar: Calendar = Calendar.getInstance()

    private var filteredEnquiry: List<JobBidListing> = mutableListOf()
    private var startDate: Date? = null
    private var endDate: Date? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProviderBidsBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Utility.isInternetAvailable(requireContext())) {
            showProgressBar()
            updateStartDateField()
            updateENdDateField()

            getCustomerJobListing()
        }
        else {
            Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_SHORT).show()
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val searchText = p0.toString()

                val filteredList = filteredEnquiry.filter { marketDetail ->
                    marketDetail.service_name.contains(searchText, ignoreCase = true)
                }
                bidsAdapter.updateList(filteredList)

            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        binding.tvStartCalender.setOnClickListener {
            openStartDatePickerDialog()
        }
        binding.tvEndCalender.setOnClickListener {
            openEndDatePickerDialog()
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
        bidsAdapter  = BidsAdapter(requireContext(), filteredEnquiry)
        binding.rvBidsList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvBidsList.adapter = bidsAdapter
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        filteredEnquiry = enquiry.filter {
            it.service_name.contains(searchText, ignoreCase = true) &&
                    isWithinDateRange(sdf.parse(it.doc))
        }
        bidsAdapter.updateList(filteredEnquiry)
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
        call.enqueue(object : Callback<BookingApiResponse> {
            override fun onResponse(call: Call<BookingApiResponse>, response: Response<BookingApiResponse>) {
                if (response.isSuccessful) {
                    if (response.body()?.status == 1) {
                        if (isAdded) {
                            hideProgressBar()
                            val questionnaireResponse = response.body()
                            enquiry = questionnaireResponse?.data?.job_bid_listing ?: emptyList()

                            bidsAdapter = BidsAdapter(requireContext(), filteredEnquiry)
                            binding.rvBidsList.layoutManager =
                                LinearLayoutManager(
                                    requireContext(),
                                    LinearLayoutManager.VERTICAL,
                                    false
                                )
                            binding.rvBidsList.adapter = bidsAdapter
                            filterLists()
                            if (filteredEnquiry.isEmpty()) {
                                binding.tvNoData.visibility = View.VISIBLE
                            }
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