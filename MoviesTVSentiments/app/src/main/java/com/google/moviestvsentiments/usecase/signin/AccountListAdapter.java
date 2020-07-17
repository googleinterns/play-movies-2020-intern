package com.google.moviestvsentiments.usecase.signin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.google.moviestvsentiments.R;
import com.google.moviestvsentiments.model.Account;
import java.util.ArrayList;
import java.util.List;

/**
 * An adapter that binds a list of account objects to views for display in a RecyclerView. In
 * addition to the given accounts, an "Add Account" view will be displayed.
 */
class AccountListAdapter extends RecyclerView.Adapter<AccountListAdapter.AccountViewHolder> {

    /**
     * Provides callbacks to be invoked when an account is clicked.
     */
    interface AccountClickListener {
        /**
         * Handles an account in the account list being clicked.
         * @param addAccount True if the user has requested to add a new account. False if the user
         *                   has selected an existing account.
         * @param accountName The name of the selected account or "Add Account" if addAccount is
         *                    true.
         */
        void onAccountClick(boolean addAccount, String accountName);
    }

    /**
     * A container that holds metadata about an item view in the account list.
     */
    static class AccountViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private boolean addAccount;
        private final ImageView iconView;
        private final TextView iconTextView;
        private final TextView nameTextView;
        private final AccountClickListener accountClickListener;

        private AccountViewHolder(View itemView, AccountClickListener accountClickListener) {
            super(itemView);
            itemView.setOnClickListener(this);
            iconView = itemView.findViewById(R.id.accountIcon);
            iconTextView = itemView.findViewById(R.id.accountIconText);
            nameTextView = itemView.findViewById(R.id.accountTextView);
            this.accountClickListener = accountClickListener;
        }

        /**
         * Saves metadata about the item view and sets the item view's text.
         * @param addAccount Indicates whether clicking this item view should initiate the process
         *                   of adding a new account name.
         * @param text The text to display in the item view.
         * @param colorId The tint color to use for the icon view.
         */
        private void bind(boolean addAccount, String text, int colorId) {
            this.addAccount = addAccount;
            nameTextView.setText(text);
            if (addAccount) {
                iconTextView.setVisibility(View.GONE);
                iconView.setImageDrawable(iconView.getContext().getDrawable(R.drawable.ic_baseline_person_add_24));
                iconView.setTag(R.drawable.ic_baseline_person_add_24);
            } else {
                iconTextView.setText(text.substring(0, 1).toUpperCase());
                iconTextView.setVisibility(View.VISIBLE);
                iconView.setImageDrawable(iconView.getContext().getDrawable(R.drawable.circle));
                iconView.setTag(R.drawable.circle);
            }
            iconView.setColorFilter(ContextCompat.getColor(iconView.getContext(), colorId));
        }

        @Override
        public void onClick(View view) {
            accountClickListener.onAccountClick(addAccount, nameTextView.getText().toString());
        }
    }

    private static int[] ACCOUNT_ICON_COLORS = new int[] {R.color.red, R.color.purple, R.color.blue,
            R.color.green};

    private List<Account> accounts;
    private AccountClickListener accountClickListener;

    private AccountListAdapter(AccountClickListener accountClickListener) {
        this.accountClickListener = accountClickListener;
        accounts = new ArrayList<>();
    }

    /**
     * Creates a new AccountListAdapter.
     * @param accountClickListener The listener to invoke when an account is clicked.
     * @return A new AccountListAdapter.
     */
    static AccountListAdapter create(AccountClickListener accountClickListener) {
        return new AccountListAdapter(accountClickListener);
    }

    /**
     * Sets the adapter's list of accounts to a copy of the provided account list.
     * @param accounts The list of accounts to copy.
     */
    public void setAccounts(List<Account> accounts) {
        this.accounts = new ArrayList<>(accounts);
        notifyDataSetChanged();
    }

    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.account_list_item,
                parent, false);
        return new AccountViewHolder(itemView, accountClickListener);
    }

    @Override
    public void onBindViewHolder(AccountViewHolder holder, int position) {
        if (position < accounts.size()) {
            holder.bind(false, accounts.get(position).name(),
                    ACCOUNT_ICON_COLORS[position % ACCOUNT_ICON_COLORS.length]);
        } else {
            holder.bind(true, "Add Account", R.color.white);
        }
    }

    @Override
    public int getItemCount() {
        // Add one extra item for the Add Account item
        return accounts.size() + 1;
    }
}
