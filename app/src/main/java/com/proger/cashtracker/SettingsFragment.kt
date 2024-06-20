package com.proger.cashtracker

import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.github.orangegangsters.lollipin.lib.managers.AppLock
import com.github.orangegangsters.lollipin.lib.managers.LockManager
import com.proger.cashtracker.databinding.ActivityMainBinding
import com.proger.cashtracker.databinding.FragmentSettingsBinding
import com.proger.cashtracker.ui.screens.pin.PinCodeActivity
import com.proger.cashtracker.utils.LocalData

class SettingsFragment : Fragment() {
    private val viewModel: SettingsViewModel by viewModels()
    private lateinit var binding: FragmentSettingsBinding
    private var locker = LockManager.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater)
        val localDate = LocalData(requireContext())

        binding.checkBoxUsingPinCode.isChecked = localDate.getIsUsingPinCode()

        binding.checkBoxUsingPinCode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val intent = Intent(requireContext(), PinCodeActivity::class.java)
                intent.putExtra(AppLock.EXTRA_TYPE, AppLock.ENABLE_PINLOCK)
                startActivityForResult(intent, 101)
                localDate.setIsUsingPinCode(true)
            } else {
                locker.disableAppLock()
                Toast.makeText(requireContext(),"Disabled pin code successfully",Toast.LENGTH_LONG).show()
                localDate.setIsUsingPinCode(false)
            }
        }
        return binding.root
    }
}