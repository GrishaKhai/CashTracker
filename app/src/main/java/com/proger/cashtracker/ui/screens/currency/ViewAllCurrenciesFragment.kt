package com.proger.cashtracker.ui.screens.currency

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.proger.cashtracker.R
import com.proger.cashtracker.databinding.FragmentViewTransactionBinding
import com.proger.cashtracker.utils.LocalData
import com.proger.cashtracker.models.Currency
import com.proger.cashtracker.ui.elements.recyclerView.CurrencyAdapter
import com.proger.cashtracker.utils.DataStatus
import com.proger.cashtracker.utils.isVisible
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ViewAllCurrenciesFragment : Fragment(R.layout.fragment_view_transaction) {
    private lateinit var binding: FragmentViewTransactionBinding
    private val viewModel: ViewAllCurrenciesViewModel by viewModels()
    @Inject
    lateinit var currencyAdapter: CurrencyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)//для того что бы дополнить меню
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.refresh_toolbar, menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> {
                viewModel.updateCurrencyNow(LocalData(requireContext()))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @SuppressLint("ResourceType")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentViewTransactionBinding.inflate(inflater)
        binding.apply {
            viewModel.updateCurrency()
            viewModel.currencies.observe(viewLifecycleOwner) { currencies ->
                when (currencies.status) {
                    DataStatus.Status.LOADING -> {
                        pbLoading.isVisible(true, rvTransaction)
                        emptyBody.isVisible(false, rvTransaction)
                    }

                    DataStatus.Status.SUCCESS -> {
                        currencies.isEmpty?.let { isEmpty -> showEmpty(isEmpty) }
                        pbLoading.isVisible(false, rvTransaction)

                        val ld = LocalData(requireContext())
                        val baseCurrencyCode = ld.getBaseCurrency()
                        currencyAdapter.setBaseCurrency(ld.getBaseCurrency()!!)
                        currencyAdapter.setUpdateBaseCurrency(object : IUpdateBaseCurrency {
                            override fun update(currency: Currency) {
                                ld.updateBaseCurrency(currency.currencyCode)
                                viewModel.updateCurrency(currency.rateToBase)
                            }
                        })
                        currencyAdapter.differ.submitList(currencies.data)
                        rvTransaction.apply {
                            layoutManager = LinearLayoutManager(context)
                            adapter = currencyAdapter
                        }

                        val baseCurrency = currencies.data!!.first { item -> item.currencyCode == baseCurrencyCode }

                        if(viewModel.getSelectedCurrency() == null) viewModel.setSelectedCurrency(baseCurrency)

                        binding.tvTotal.text = "${baseCurrency.currencyCode} - ${baseCurrency.currencyName} (${baseCurrency.rateToBase})"
                    }

                    DataStatus.Status.ERROR -> {
                        pbLoading.isVisible(false, rvTransaction)
                        Toast.makeText(context, currencies.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        return binding.root
    }

    private fun showEmpty(isShown: Boolean) {
        binding.apply {
            if (isShown) {
                emptyBody.visibility = View.VISIBLE
                clListBody.visibility = View.GONE
            } else {
                emptyBody.visibility = View.GONE
                clListBody.visibility = View.VISIBLE
            }
        }
    }
}