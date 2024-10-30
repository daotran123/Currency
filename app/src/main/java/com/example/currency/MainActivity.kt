package com.example.currency

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var editTextSourceAmount: EditText
    private lateinit var editTextTargetAmount: EditText
    private lateinit var spinnerSourceCurrency: Spinner
    private lateinit var spinnerTargetCurrency: Spinner
    private lateinit var textViewRates: TextView

    private val currencyRates = mapOf(
        "USD" to 1.0,
        "EUR" to 0.85,
        "JPY" to 110.0,
        "VND" to 27433.0
    )

    private var sourceCurrency = "VND"
    private var targetCurrency = "EUR"

    // Biến kiểm soát
    private var isSourceUpdating = false
    private var isTargetUpdating = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextSourceAmount = findViewById(R.id.editTextSourceAmount)
        editTextTargetAmount = findViewById(R.id.editTextTargetAmount)
        spinnerSourceCurrency = findViewById(R.id.spinnerSourceCurrency)
        spinnerTargetCurrency = findViewById(R.id.spinnerTargetCurrency)
        textViewRates = findViewById(R.id.textViewRates)

        setupSpinners()
        setupListeners()
    }

    private fun setupSpinners() {
        val currencyList = currencyRates.keys.toList()

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencyList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerSourceCurrency.adapter = adapter
        spinnerTargetCurrency.adapter = adapter

        spinnerSourceCurrency.setSelection(currencyList.indexOf(sourceCurrency))
        spinnerTargetCurrency.setSelection(currencyList.indexOf(targetCurrency))
    }

    private fun setupListeners() {
        editTextSourceAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!isSourceUpdating) {
                    updateConversionFromSource()
                }
            }
        })

        editTextTargetAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!isTargetUpdating) {
                    updateConversionFromTarget()
                }
            }
        })

        spinnerSourceCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                sourceCurrency = parent?.getItemAtPosition(position) as String
                updateConversionFromSource()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerTargetCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                targetCurrency = parent?.getItemAtPosition(position) as String
                updateConversionFromTarget()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun updateConversionFromSource() {
        val amount = try {
            editTextSourceAmount.text.toString().toDoubleOrNull() ?: return
        } catch (e: NumberFormatException) {
            return
        }

        val sourceRate = currencyRates[sourceCurrency] ?: return
        val targetRate = currencyRates[targetCurrency] ?: return

        val convertedAmount = amount * (targetRate / sourceRate)

        // Tạm tắt TextWatcher
        isTargetUpdating = true
        editTextTargetAmount.setText(String.format("%.2f", convertedAmount))
        isTargetUpdating = false

        textViewRates.text = "1 $sourceCurrency = ${targetRate / sourceRate} $targetCurrency"
    }

    private fun updateConversionFromTarget() {
        val amount = try {
            editTextTargetAmount.text.toString().toDoubleOrNull() ?: return
        } catch (e: NumberFormatException) {
            return
        }

        val sourceRate = currencyRates[sourceCurrency] ?: return
        val targetRate = currencyRates[targetCurrency] ?: return

        val convertedAmount = amount * (sourceRate / targetRate)

        // Tạm tắt TextWatcher
        isSourceUpdating = true
        editTextSourceAmount.setText(String.format("%.2f", convertedAmount))
        isSourceUpdating = false

        textViewRates.text = "1 $targetCurrency = ${sourceRate / targetRate} $sourceCurrency"
    }
}
