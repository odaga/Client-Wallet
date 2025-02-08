package com.mcash.client.presentation.ui.contacts

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.mcash.client.R
import com.mcash.client.core.sealed.ContactState
import com.mcash.client.databinding.ActivityContactsBinding
import com.mcash.client.presentation.adapters.ContactsAdapter
import com.mcash.client.core.utils.Constants.SELECTED_CONTACT
import com.mcash.client.core.utils.showKeyboard
import com.mcash.domain.model.Contact
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class ContactsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContactsBinding

    private val viewModel: ContactViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getContacts()

        with(binding) {
            civBack.setImageResource(R.drawable.ic_close)
            btnBack.setOnClickListener { finish() }
            mtvTitle.text = getString(R.string.your_contacts)

            etSearch.doOnTextChanged { text, _, _, _ ->
                if (text.toString().isNotEmpty()) {

                    if (text.toString().length > 1) {
                        viewModel.searchContact("%${text.toString().trim()}%")
                    } else {
                        viewModel.getContacts()
                    }
                }
            }

            btnBack.setOnClickListener {
                if (etSearch.isVisible) {
                    mtvTitle.visibility = View.VISIBLE
                    etSearch.visibility = View.GONE
                } else {
                    finish()
                }
            }

            btnSearch.setOnClickListener {
                etSearch.visibility = View.VISIBLE
                mtvTitle.visibility = View.GONE
                etSearch.requestFocus()
                etSearch.showKeyboard()
            }
        }


        lifecycleScope.launchWhenStarted {
            viewModel.contactsState.collect {
                when(it) {
                    is ContactState.Loading -> {}
                    is ContactState.Error ->  Timber.d("Contact error: ${it.message}")
                    is ContactState.Success -> {
                        Timber.d("List of contacts: ${it.data}")

                        val list = ArrayList(it.data)
                        val contactsAdapter = ContactsAdapter(
                            this@ContactsActivity,
                            object: ContactsAdapter.ContactsClickListener{
                                override fun onContactClicked(contact: Contact) {
                                    val intent = Intent()
                                    val obj = Gson().toJson(contact)
                                    intent.putExtra(SELECTED_CONTACT, obj)
                                    setResult(RESULT_OK, intent)
                                    finish()
                                }

                            }
                        )
                        contactsAdapter.contacts = list
                        binding.contactsRecycler.apply {
                            adapter = contactsAdapter
                            layoutManager = LinearLayoutManager(this@ContactsActivity,  LinearLayoutManager.VERTICAL, false)
                        }
                    }
                }
            }
        }

    }
}