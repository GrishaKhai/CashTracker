package com.proger.cashtracker.ui.screens.account.dialogs.transfer

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.proger.cashtracker.R

class TransferBetweenAccountsDialog : Fragment() {

    companion object {
        fun newInstance() = TransferBetweenAccountsDialog()
    }

    private val viewModel: TransferBetweenAccountsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.dialog_transfer_between_accounts, container, false)
    }
}