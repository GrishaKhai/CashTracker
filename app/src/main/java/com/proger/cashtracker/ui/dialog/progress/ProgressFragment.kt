package com.proger.cashtracker.ui.dialog.progress

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.proger.cashtracker.databinding.FragmentProgressBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class ProgressFragment : DialogFragment() {
    private lateinit var binding: FragmentProgressBinding
    private lateinit var text: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProgressBinding.inflate(inflater)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        binding.tvMessageProgress.text = text
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setCanceledOnTouchOutside(false)
        }
    }

    override fun show(manager: FragmentManager, tag: String?) {
        super.show(manager, tag)
        text = tag!!
    }
}