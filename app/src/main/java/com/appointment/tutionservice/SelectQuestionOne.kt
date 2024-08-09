package com.appointment.tutionservice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.appointment.tutionservice.databinding.ActivitySelectQuestionOneBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SelectQuestionOne : AppCompatActivity() {

    private lateinit var binding: ActivitySelectQuestionOneBinding
    private lateinit var questionnaireList: List<Questionnaire>
    private var currentIndex = 0
    private lateinit var adapter: RadioBtnAdpter
    var textRequirements = ""
    var userResponses: MutableList<QuestionnaireAnswer> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectQuestionOneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getServiceDetails()
        binding.ivBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }


        binding.btnNext.setOnClickListener {
            if (binding.etRequirements.text.toString().isEmpty() && binding.textRequirements.visibility == View.VISIBLE) {
                Toast.makeText(
                    this,
                    "Please fill in all fields",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else{
            currentIndex++
            textRequirements = binding.etRequirements.text.toString()
            if (currentIndex < questionnaireList.size) {
                displayQuestionnaireItem(currentIndex)

                Log.i("TAG", "questionnaireList: " + Constant.questionnaireList)
            } else {
                if (currentIndex == questionnaireList.size) {
                    if (textRequirements.isNotBlank()) {
                        userResponses.add(QuestionnaireAnswer(Constant.QuestionId, textRequirements))
                    }
                    if (Constant.ANSWER.isNotBlank()) {
                        userResponses.add(QuestionnaireAnswer(Constant.QuestionId, Constant.ANSWER))
                    }

                    if (Constant.ENQUIRY == "enquiry") {
                    hideProgressBar()
                    val intent = Intent(applicationContext, EnquiryLastStepActivity::class.java)
                    startActivity(intent)
                    Log.i("TAG", "displayQuestionnaireItem: " + Constant.questionnaireList)

                } else {
                    hideProgressBar()
                    val intent = Intent(applicationContext, SelectquestionThree::class.java)
                    startActivity(intent)
                }
            }
            }
        }
        }

    }

    private fun getServiceDetails() {
        showProgressBar()

        val call = RetrofitClient.api.getServiceDetails(
            Constant.MOBILE_NO,
            Constant.API_KEY,
            Utility.getDeviceId(applicationContext),
            Utility.deviceToken,
            Constant.APP_USER_KEY,
            Constant.APP_USER_ID.toString(),
            Constant.APP_VERSION_NAME.toString(), Constant.SERVICE_ID
        )
        call.enqueue(object : Callback<QuestionnaireResponse> {
            override fun onResponse(call: Call<QuestionnaireResponse>, response: Response<QuestionnaireResponse>) {
                if (response.isSuccessful) {
                    if (response.body()?.status == 1) {
                        hideProgressBar()
                        val questionnaireResponse = response.body()
                        questionnaireResponse?.let { it ->
                            questionnaireList = it.questionnaires
                            displayQuestionnaireItem(currentIndex)
                            val radioBtnQuestion: List<QuestionOptionValue> = questionnaireResponse.questionnaires
                                .filter { it.type_of_question == "Radio Button" }
                                .flatMap { it.question_option_values ?: emptyList()
                                }

                            adapter = RadioBtnAdpter(radioBtnQuestion, applicationContext)
                            binding.rvRadioBtn.adapter = adapter
                            binding.rvRadioBtn.layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
                        }

                        Log.i("TAG", "getServiceNameByType: ${response.body()}")
                    }
                } else {
                    hideProgressBar()
                    Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT).show()
                    Log.i("TAG", "getServiceNameByType: ${response.body()}")
                }
            }

            override fun onFailure(call: Call<QuestionnaireResponse>, t: Throwable) {
                hideProgressBar()
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
                Log.i("TAG", "getServiceNameByType: ${t.message}")

            }
        })
    }

    private fun displayQuestionnaireItem(index: Int) {
        if (questionnaireList.isNotEmpty()) {
            val questionnaire = questionnaireList[index]
            if (textRequirements.isNotBlank()) {
                userResponses.add(QuestionnaireAnswer(Constant.QuestionId, textRequirements))
            }
            if (Constant.ANSWER.isNotBlank()) {
                userResponses.add(
                    QuestionnaireAnswer(
                        Constant.QuestionId,
                        Constant.ANSWER
                    )
                )
            }

            val data = userResponses
            Log.i("TAG", "displayQuestionnaireItem: " + data)
            Constant.questionnaireList = userResponses

            if (questionnaire.type_of_question == "Text") {
                Constant.QuestionId = questionnaire.questionnaire_id
                binding.etRequirements.text.clear()
                Constant.ANSWER = ""
                binding.tellUsMore.text = questionnaire.question
                binding.textRequirements.visibility = View.VISIBLE
                binding.radioBtnCard.visibility = View.GONE

            } else if (questionnaire.type_of_question == "Radio Button") {
                binding.etRequirements.text.clear()
                Constant.ANSWER = ""
                Constant.QuestionId = questionnaire.questionnaire_id
                binding.textRequirements.visibility = View.GONE
                binding.radioBtnCard.visibility = View.VISIBLE
                binding.questionDescriptiom.text = questionnaire.question
            }
        }
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