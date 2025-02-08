package com.mcash.client.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mcash.client.databinding.RowContactDataBinding
import com.mcash.domain.model.Contact

class ContactsAdapter(
    private val context: Context,
    private val contactsClickListener: ContactsClickListener
) : RecyclerView.Adapter<ContactsAdapter.MyViewHolder>() {

    val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    var contacts = ArrayList<Contact>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(RowContactDataBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount() = contacts.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val contact = contacts[position]
        with(holder.binding) {
            tvContactName.text = contact.name
            if(contact.numbers.isNotEmpty()) {
                tvContactPhone.text = contact.numbers[0]
            }

            root.setOnClickListener { contactsClickListener.onContactClicked(contact) }
        }
    }

    inner class MyViewHolder(val binding: RowContactDataBinding) : RecyclerView.ViewHolder(binding.root)

    interface ContactsClickListener {
        fun onContactClicked(contact: Contact)
    }
}