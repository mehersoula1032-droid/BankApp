package com.example.bankapp;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import java.util.List;
import java.util.Locale;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private LinearLayout layoutLogin, layoutRegister, layoutDashboard;
    private EditText etCin, etPassword, etNewCin, etFirstName, etLastName, etEmail, etRegPassword, etAmount, etCinDestinataire;
    private AppCompatButton btnLogin, btnRegister, btnTransfer;
    private TextView btnShowRegister, btnForgotPassword, btnTutorial;
    private ImageView btnLogout, btnNotifications;
    private TextView tvWelcome, tvSolde, tvSoldeEuro, tvStatus, tvHistorique, tvCardNumber, tvCardType;
    private Spinner spinnerLang;

    private CustomerModel loggedCustomer = null;
    private CardModel myCard = null;
    private String currentStatus = "PENDING";
    private SharedPreferences prefs;
    private SharedPreferences langPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        langPrefs = getSharedPreferences("Settings", MODE_PRIVATE);
        String savedLang = langPrefs.getString("My_Lang", "fr");
        Locale locale = new Locale(savedLang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        setContentView(R.layout.activity_main);
        prefs = getSharedPreferences("BankAppPrefs", MODE_PRIVATE);

        layoutLogin = findViewById(R.id.layoutLogin);
        layoutRegister = findViewById(R.id.layoutRegister);
        layoutDashboard = findViewById(R.id.layoutDashboard);

        etCin = findViewById(R.id.etCin);
        etPassword = findViewById(R.id.etPassword);
        etNewCin = findViewById(R.id.etNewCin);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        etRegPassword = findViewById(R.id.etRegPassword);
        etAmount = findViewById(R.id.etAmount);
        etCinDestinataire = findViewById(R.id.etCinDestinataire);

        btnLogin = findViewById(R.id.btnLogin);
        btnShowRegister = findViewById(R.id.btnShowRegister);
        btnRegister = findViewById(R.id.btnRegister);
        btnTransfer = findViewById(R.id.btnTransfer);
        btnForgotPassword = findViewById(R.id.btnForgotPassword);
        btnTutorial = findViewById(R.id.btnTutorial);

        btnLogout = findViewById(R.id.btnLogout);
        btnNotifications = findViewById(R.id.btnNotifications);

        tvWelcome = findViewById(R.id.tvWelcome);
        tvSolde = findViewById(R.id.tvSolde);
        tvSoldeEuro = findViewById(R.id.tvSoldeEuro);
        tvStatus = findViewById(R.id.tvStatus);
        tvHistorique = findViewById(R.id.tvHistorique);
        tvCardNumber = findViewById(R.id.tvCardNumber);
        tvCardType = findViewById(R.id.tvCardType);

        spinnerLang = findViewById(R.id.spinnerLang);
        tvHistorique.setMovementMethod(new ScrollingMovementMethod());

        String[] languages = {"Français", "English", "العربية"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLang.setAdapter(adapter);

        if (savedLang.equals("en")) spinnerLang.setSelection(1);
        else if (savedLang.equals("ar")) spinnerLang.setSelection(2);
        else spinnerLang.setSelection(0);

        spinnerLang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean isFirst = true;
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isFirst) { isFirst = false; return; }
                String newLang = "fr";
                if (position == 1) newLang = "en";
                if (position == 2) newLang = "ar";
                if (!newLang.equals(langPrefs.getString("My_Lang", "fr"))) {
                    langPrefs.edit().putString("My_Lang", newLang).apply();
                    recreate();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnShowRegister.setOnClickListener(v -> {
            layoutLogin.setVisibility(View.GONE);
            layoutRegister.setVisibility(View.VISIBLE);
        });

        btnLogin.setOnClickListener(v -> {
            String cin = etCin.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();
            if (cin.isEmpty() || pass.isEmpty()) return;
            performLogin(cin, pass);
        });

        btnForgotPassword.setOnClickListener(v -> {
            EditText inputCin = new EditText(this);
            inputCin.setHint(getString(R.string.hint_enter_cin));
            inputCin.setInputType(InputType.TYPE_CLASS_NUMBER);
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.dialog_forgot_title))
                    .setMessage(getString(R.string.dialog_forgot_msg))
                    .setView(inputCin)
                    .setPositiveButton(getString(R.string.btn_send), (dialog, which) -> {
                        String cin = inputCin.getText().toString().trim();
                        if (cin.isEmpty()) return;
                        RetrofitClient.getCustomerApi().getByCin(cin).enqueue(new Callback<CustomerModel>() {
                            @Override
                            public void onResponse(Call<CustomerModel> call, Response<CustomerModel> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    CustomerModel customer = response.body();
                                    String otp = String.format("%06d", new Random().nextInt(999999));
                                    showOtpDialog(customer, otp);
                                } else {
                                    Toast.makeText(MainActivity.this, "CIN introuvable", Toast.LENGTH_SHORT).show();
                                }
                            }
                            @Override
                            public void onFailure(Call<CustomerModel> call, Throwable t) {
                                Toast.makeText(MainActivity.this, "Erreur réseau: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton(getString(R.string.btn_cancel), null)
                    .show();
        });

        btnTutorial.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.dialog_help_title))
                    .setMessage(getString(R.string.dialog_help_msg))
                    .setPositiveButton(getString(R.string.btn_ok), null)
                    .show();
        });

        btnRegister.setOnClickListener(v -> {
            String cin = etNewCin.getText().toString().trim();
            String fn = etFirstName.getText().toString().trim();
            String ln = etLastName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String pass = etRegPassword.getText().toString().trim();

            if (cin.length() != 8 || pass.length() < 4) {
                Toast.makeText(this, "CIN (8 chiffres) et Mot de passe (min 4) requis", Toast.LENGTH_LONG).show();
                return;
            }
            if (email.isEmpty() || !email.contains("@")) {
                Toast.makeText(this, "Email invalide", Toast.LENGTH_SHORT).show();
                return;
            }

            CustomerModel newCust = new CustomerModel();
            newCust.setCin(cin);
            newCust.setFirstName(fn);
            newCust.setLastName(ln);
            newCust.setEmail(email);
            newCust.setPassword(pass);

            RetrofitClient.getCustomerApi().create(newCust).enqueue(new Callback<CustomerModel>() {
                @Override
                public void onResponse(Call<CustomerModel> call, Response<CustomerModel> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        loggedCustomer = response.body();
                        prefs.edit().putString("logged_cin", cin).apply();
                        prefs.edit().putString("logged_pass", pass).apply();

                        AccountModel newAccount = new AccountModel();
                        newAccount.setCustomerId(loggedCustomer.getId());
                        newAccount.setBalance(0.0);
                        newAccount.setStatus("PENDING");

                        RetrofitClient.getAccountApi().create(newAccount).enqueue(new Callback<AccountModel>() {
                            @Override
                            public void onResponse(Call<AccountModel> call, Response<AccountModel> accResponse) {
                                if (accResponse.isSuccessful()) {
                                    CardModel newCard = new CardModel();
                                    newCard.setCustomerId(loggedCustomer.getId());
                                    newCard.setCardNumber("4532" + (1000 + (int)(Math.random() * 9000)) + "1234");
                                    newCard.setCardType("VISA");
                                    newCard.setExpiryDate("12/28");
                                    newCard.setCvv(String.valueOf(100 + (int)(Math.random() * 900)));

                                    RetrofitClient.getCardApi().createCard(newCard).enqueue(new Callback<CardModel>() {
                                        @Override
                                        public void onResponse(Call<CardModel> call, Response<CardModel> cardResp) {
                                            Toast.makeText(MainActivity.this, "Compte créé ! En attente de validation.", Toast.LENGTH_LONG).show();
                                            loadDashboard();
                                        }
                                        @Override
                                        public void onFailure(Call<CardModel> call, Throwable t) { loadDashboard(); }
                                    });
                                } else { loadDashboard(); }
                            }
                            @Override
                            public void onFailure(Call<AccountModel> call, Throwable t) { loadDashboard(); }
                        });
                    } else {
                        Toast.makeText(MainActivity.this, "Erreur (CIN existant?)", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<CustomerModel> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Erreur register: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });

        btnTransfer.setOnClickListener(v -> {
            if (!checkActiveStatus()) return;

            if (myCard == null) {
                Toast.makeText(this, "Erreur : Carte introuvable.", Toast.LENGTH_LONG).show();
                return;
            }

            String cinDest = etCinDestinataire.getText().toString().trim();
            String amtStr = etAmount.getText().toString().trim();
            if (cinDest.isEmpty() || amtStr.isEmpty()) return;
            double amount = Double.parseDouble(amtStr.replace(',', '.'));

            new AlertDialog.Builder(this)
                    .setTitle("Confirmation de Virement")
                    .setMessage("Voulez-vous vraiment transférer " + amount + " TND vers le CIN : " + cinDest + " ?")
                    .setPositiveButton("Confirmer", (dialogConfirm, whichConfirm) -> {
                        showCvvDialog(cinDest, amount);
                    })
                    .setNegativeButton("Annuler", (dialogConfirm, whichConfirm) -> {
                        Toast.makeText(MainActivity.this, "Transfert annulé", Toast.LENGTH_SHORT).show();
                    })
                    .show();
        });

        btnLogout.setOnClickListener(v -> {
            loggedCustomer = null;
            myCard = null;
            prefs.edit().remove("logged_cin").apply();
            prefs.edit().remove("logged_pass").apply();
            layoutDashboard.setVisibility(View.GONE);
            layoutLogin.setVisibility(View.VISIBLE);
            etCin.setText("");
            etPassword.setText("");
            etNewCin.setText("");
            etFirstName.setText("");
            etLastName.setText("");
            etEmail.setText("");
            etRegPassword.setText("");
            Toast.makeText(this, "Déconnecté", Toast.LENGTH_SHORT).show();
        });

        btnNotifications.setOnClickListener(v -> {
            if (loggedCustomer == null) return;
            RetrofitClient.getNotificationApi().getNotifications(loggedCustomer.getId()).enqueue(new Callback<List<NotificationModel>>() {
                @Override
                public void onResponse(Call<List<NotificationModel>> call, Response<List<NotificationModel>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().isEmpty()) {
                            Toast.makeText(MainActivity.this, "Aucune notification.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        StringBuilder sb = new StringBuilder();
                        for (NotificationModel n : response.body()) {
                            sb.append("🔔 ").append(n.getMessage()).append("\n\n");
                        }
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Mes Notifications")
                                .setMessage(sb.toString())
                                .setPositiveButton("OK", null)
                                .show();
                    }
                }
                @Override
                public void onFailure(Call<List<NotificationModel>> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Erreur notifications", Toast.LENGTH_SHORT).show();
                }
            });
        });

        String savedCin = prefs.getString("logged_cin", null);
        String savedPass = prefs.getString("logged_pass", null);
        if (savedCin != null && savedPass != null) {
            performLogin(savedCin, savedPass);
        }
    }

    private void showCvvDialog(String cinDest, double amount) {
        AlertDialog.Builder cvvBuilder = new AlertDialog.Builder(this);
        cvvBuilder.setTitle("Sécurité");
        cvvBuilder.setMessage("Entrez le CVV de votre carte (3 chiffres)");
        final EditText inputCvv = new EditText(this);
        inputCvv.setInputType(InputType.TYPE_CLASS_NUMBER);
        cvvBuilder.setView(inputCvv);

        cvvBuilder.setPositiveButton("Valider", (dialogCvv, whichCvv) -> {
            String cvv = inputCvv.getText().toString().trim();
            if (!myCard.getCvv().equals(cvv)) {
                Toast.makeText(MainActivity.this, "CVV Incorrect!", Toast.LENGTH_SHORT).show();
                return;
            }
            executeTransfer(cinDest, amount);
        });
        cvvBuilder.setNegativeButton("Annuler", (dialogCvv, whichCvv) -> cvvBuilder.show().cancel());
        cvvBuilder.show();
    }

    private void executeTransfer(String cinDest, double amount) {
        RetrofitClient.getCustomerApi().getByCin(cinDest).enqueue(new Callback<CustomerModel>() {
            @Override
            public void onResponse(Call<CustomerModel> call, Response<CustomerModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CustomerModel destinataire = response.body();
                    Long destId = destinataire.getId();

                    RetrofitClient.getAccountApi().transfer(loggedCustomer.getId(), destId, amount).enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (response.isSuccessful()) {

                                TransactionModel txEnvoye = new TransactionModel();
                                txEnvoye.setCustomerId(loggedCustomer.getId());
                                txEnvoye.setType("VIREMENT_ENVOYE");
                                txEnvoye.setAmount(amount);
                                txEnvoye.setDescription("Virement vers CIN: " + cinDest);

                                RetrofitClient.getTransactionApi().create(txEnvoye).enqueue(new Callback<TransactionModel>() {
                                    @Override
                                    public void onResponse(Call<TransactionModel> call, Response<TransactionModel> resp) {

                                        TransactionModel txRecu = new TransactionModel();
                                        txRecu.setCustomerId(destId);
                                        txRecu.setType("VIREMENT_RECU");
                                        txRecu.setAmount(amount);
                                        txRecu.setDescription("Virement reçu de CIN: " + loggedCustomer.getCin());

                                        RetrofitClient.getTransactionApi().create(txRecu).enqueue(new Callback<TransactionModel>() {
                                            @Override
                                            public void onResponse(Call<TransactionModel> call, Response<TransactionModel> resp2) {

                                                NotificationModel notifEnvoyeur = new NotificationModel();
                                                notifEnvoyeur.setCustomerId(loggedCustomer.getId());
                                                notifEnvoyeur.setMessage("Soula Bank - Confirmation:\nVous avez envoyé " + amount + " TND à CIN: " + cinDest);
                                                notifEnvoyeur.setEmail(loggedCustomer.getEmail());

                                                RetrofitClient.getNotificationApi().create(notifEnvoyeur).enqueue(new Callback<NotificationModel>() {
                                                    @Override public void onResponse(Call<NotificationModel> call, Response<NotificationModel> response) {}
                                                    @Override public void onFailure(Call<NotificationModel> call, Throwable t) {}
                                                });

                                                NotificationModel notifReceveur = new NotificationModel();
                                                notifReceveur.setCustomerId(destId);
                                                notifReceveur.setMessage("Soula Bank - Confirmation:\nVous avez reçu " + amount + " TND de CIN: " + loggedCustomer.getCin());
                                                notifReceveur.setEmail(destinataire.getEmail());

                                                RetrofitClient.getNotificationApi().create(notifReceveur).enqueue(new Callback<NotificationModel>() {
                                                    @Override
                                                    public void onResponse(Call<NotificationModel> call, Response<NotificationModel> response) {
                                                        Toast.makeText(MainActivity.this, "Virement réussi, notification et email envoyés !", Toast.LENGTH_LONG).show();
                                                        etCinDestinataire.setText("");
                                                        etAmount.setText("");
                                                        loadDashboard();
                                                    }
                                                    @Override
                                                    public void onFailure(Call<NotificationModel> call, Throwable t) {
                                                        loadDashboard();
                                                    }
                                                });
                                            }
                                            @Override public void onFailure(Call<TransactionModel> call, Throwable t) { loadDashboard(); }
                                        });
                                    }
                                    @Override public void onFailure(Call<TransactionModel> call, Throwable t) { loadDashboard(); }
                                });

                            } else {
                                Toast.makeText(MainActivity.this, "Erreur transfert, solde insuffisant", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Toast.makeText(MainActivity.this, "Erreur réseau transfert", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(MainActivity.this, "Destinataire introuvable", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<CustomerModel> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Erreur réseau destinataire", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showOtpDialog(CustomerModel customer, String otp) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.dialog_otp_title));
        builder.setMessage(getString(R.string.dialog_otp_msg, otp));

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);

        final EditText etOtp = new EditText(this);
        etOtp.setHint(getString(R.string.hint_otp_code));
        etOtp.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(etOtp);

        final EditText etNewPass = new EditText(this);
        etNewPass.setHint(getString(R.string.hint_new_pass));
        etNewPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(etNewPass);

        builder.setView(layout);

        builder.setPositiveButton(getString(R.string.btn_validate), (dialog, which) -> {
            String inputOtp = etOtp.getText().toString().trim();
            String newPass = etNewPass.getText().toString().trim();

            if (inputOtp.equals(otp) && newPass.length() >= 4) {
                RetrofitClient.getCustomerApi().updatePassword(customer.getId(), newPass).enqueue(new Callback<CustomerModel>() {
                    @Override
                    public void onResponse(Call<CustomerModel> call, Response<CustomerModel> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Mot de passe mis à jour !", Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<CustomerModel> call, Throwable t) {}
                });
            } else {
                Toast.makeText(MainActivity.this, "Code incorrect ou mot de passe trop court", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(getString(R.string.btn_cancel), null);
        builder.show();
    }

    private void performLogin(String cin, String password) {
        RetrofitClient.getCustomerApi().getByCin(cin).enqueue(new Callback<CustomerModel>() {
            @Override
            public void onResponse(Call<CustomerModel> call, Response<CustomerModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CustomerModel customer = response.body();
                    if (customer.getPassword() != null && customer.getPassword().equals(password)) {
                        loggedCustomer = customer;
                        prefs.edit().putString("logged_cin", cin).apply();
                        prefs.edit().putString("logged_pass", password).apply();
                        loadDashboard();
                    } else {
                        Toast.makeText(MainActivity.this, "Mot de passe incorrect!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Client introuvable!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<CustomerModel> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Erreur réseau login: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean checkActiveStatus() {
        if ("PENDING".equals(currentStatus)) {
            Toast.makeText(this, "Compte en attente de validation par l'Admin!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void loadDashboard() {
        RetrofitClient.getAccountApi().getBalance(loggedCustomer.getId()).enqueue(new Callback<AccountModel>() {
            @Override
            public void onResponse(Call<AccountModel> call, Response<AccountModel> response) {
                layoutLogin.setVisibility(View.GONE);
                layoutRegister.setVisibility(View.GONE);
                layoutDashboard.setVisibility(View.VISIBLE);
                tvWelcome.setText("Bienvenue, " + loggedCustomer.getFirstName());
                if (response.isSuccessful() && response.body() != null) {
                    AccountModel acc = response.body();
                    double bal = acc.getBalance();
                    double balEuro = bal * 0.3;

                    tvSolde.setText(String.format(Locale.US, "%.2f TND", bal));
                    tvSoldeEuro.setText(String.format(Locale.US, "%.2f EUR", balEuro));

                    currentStatus = acc.getStatus();
                    tvStatus.setText("Status: " + currentStatus);
                    tvStatus.setTextColor("ACTIVE".equals(currentStatus) ? 0xFF4CAF50 : 0xFFFF9800);
                }
                loadHistory();
                loadCard();
            }
            @Override
            public void onFailure(Call<AccountModel> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Balance Err: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadHistory() {
        RetrofitClient.getTransactionApi().getTransactions(loggedCustomer.getId()).enqueue(new Callback<List<TransactionModel>>() {
            @Override
            public void onResponse(Call<List<TransactionModel>> call, Response<List<TransactionModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    StringBuilder sb = new StringBuilder();
                    for (TransactionModel tx : response.body()) {
                        String date = tx.getDateOperation() != null ? tx.getDateOperation().replace("T", " ").substring(0, 16) : "";
                        sb.append(date).append(" | ").append(tx.getType()).append(" : ").append(tx.getAmount()).append(" TND\n");
                    }
                    if (sb.length() == 0) sb.append("Aucune transaction.");
                    tvHistorique.setText(sb.toString());
                } else { tvHistorique.setText("Erreur historique."); }
            }
            @Override
            public void onFailure(Call<List<TransactionModel>> call, Throwable t) { tvHistorique.setText("Erreur réseau."); }
        });
    }

    private void loadCard() {
        RetrofitClient.getCardApi().getCards(loggedCustomer.getId()).enqueue(new Callback<List<CardModel>>() {
            @Override
            public void onResponse(Call<List<CardModel>> call, Response<List<CardModel>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    myCard = response.body().get(0);
                    String num = myCard.getCardNumber();
                    String masked = "**** **** **** " + num.substring(num.length() - 4);
                    tvCardNumber.setText(masked + "\nCVV: " + myCard.getCvv());
                    tvCardType.setText(myCard.getCardType());
                }
            }

            @Override
            public void onFailure(Call<List<CardModel>> call, Throwable t) {
                // تم إضافة onFailure بشكل صحيح مع دالة onResponse السابقة
            }
        });
    }
}