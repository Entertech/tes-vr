package com.entertech.tes.vr


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import com.entertech.tes.ble.device.ModeType

class SetArgDialogFragment(
    private val onConfirm: (String, Double, Int, Int) -> Unit
) : DialogFragment() {

    var modetype: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.set_arg_dialog_fragment, container, false)

        val modeSpinner: Spinner = view.findViewById(R.id.mode_spinner)
        val currentInput: EditText = view.findViewById(R.id.current_input)
        val timeInput: EditText = view.findViewById(R.id.time_input)
        val frequencyInput: EditText = view.findViewById(R.id.frequency_input)
        val confirmButton: Button = view.findViewById(R.id.confirm_button)
        val cancelButton: Button = view.findViewById(R.id.cancel_button)

        // 模式设置 Spinner 数据
        val modes = ModeType.getValidType().map { it.des }
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, modes)
        modeSpinner.adapter = adapter
        modeSpinner.setSelection(0)
        modeSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View, position: Int, id: Long
            ) {
                // 获取选择的项
                modetype = modes[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // 没有选择任何项
                modeSpinner.setSelection(0)
                modetype = modes[0]
            }
        }
        confirmButton.setOnClickListener {
            onConfirm(
                modetype,
                currentInput.text.toString().toDoubleOrNull() ?: 0.0,
                timeInput.text.toString().toIntOrNull() ?: 0,
                frequencyInput.text.toString().toIntOrNull() ?: 0
            )
            dismiss()
        }

        cancelButton.setOnClickListener {
            dismiss()
        }

        return view
    }
}