package com.example.noahrickles.waterreporterteam14_harambelovedwaters.controllers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.noahrickles.waterreporterteam14_harambelovedwaters.R;
import com.example.noahrickles.waterreporterteam14_harambelovedwaters.model.Singleton;
import com.example.noahrickles.waterreporterteam14_harambelovedwaters.model.User;
import com.example.noahrickles.waterreporterteam14_harambelovedwaters.model.UserType;

import java.util.Map;
import java.util.Set;

/**
 * A login screen that offers login via email/password.
 */
public class RegistrationActivity extends AppCompatActivity {

        /**
         * Keep track of the registration task to ensure we can cancel it if requested.
         */
    private UserRegistrationTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mUsernameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    private final Singleton instance = Singleton.getInstance();
    private final Map<String, String> registeredUserMap = instance.getRegisteredUserMap();
    private final Set<User> registeredUserSet = instance.getRegisteredUserSet();

    /**
     * Actions that occur when RegistrationActivity is prompted.
     * @param savedInstanceState     the previously saved state of an instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mUsernameView = (EditText) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptRegistration();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegistration();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    /**
     * Attempts to register the account specified by the registration form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptRegistration() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid username.
        if (!instance.isUsernameValid(username)) {
            mUsernameView.setError(getString(R.string.error_invalid_username));
            focusView = mUsernameView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (!instance.isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!instance.isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        int buttonChecked = radioGroup.getCheckedRadioButtonId();
        boolean checked = false;
        if (buttonChecked != -1) {
            checked = true;
        } else {
            mPasswordView.setError(getString(R.string.error_user_type_not_selected));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt registration and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user registration attempt.
            showProgress(true);
            switch (buttonChecked) {
                //see which type of user was chosen
                case R.id.user_button:
                    if (checked) {
                        mAuthTask = new UserRegistrationTask(new User(email,
                            username, password, registeredUserSet.size(), UserType.USER));
                    }
                    break;
                case R.id.worker_button:
                    if (checked) {
                        mAuthTask = new UserRegistrationTask(new User(email,
                            username, password, registeredUserSet.size(), UserType.WORKER));
                    }
                    break;
                case R.id.manager_button:
                    if (checked) {
                        mAuthTask = new UserRegistrationTask(new User(email,
                            username, password, registeredUserSet.size(), UserType.MANAGER));
                    }
                    break;
                case R.id.administrator_button:
                    if (checked) {
                        mAuthTask = new UserRegistrationTask(
                            new User(email, username, password, registeredUserSet.size(),
                                    UserType.ADMINISTRATOR));
                    }
                    break;
            }

            mAuthTask.execute((Void) null);
        }
    }

    /**
     * Brings the user back to the welcome screen
     * @param view the cancel button
     */
    public void cancel(View view) {
        finish();
    }

    /**
     * Shows the progress UI and hides the login form.
     * @param show  boolean for whether or not to show progress
     */
    @SuppressLint("ObsoleteSdkInt")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserRegistrationTask extends AsyncTask<Void, Void, Boolean> {

        private final User user;
        private String error;

        /**
         * Constructor for the UserRegistrationTask class
         * @param user  User passed in for task
         */
        UserRegistrationTask(User user) {
            this.user = user;
        }

        /**
         * Actions to do in the background
         * @param params     background activities
         */
        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            if (registeredUserMap.containsKey(user.getEmail())) {
                error = getString(R.string.error_email_taken);
                return false;
            } else if (registeredUserMap.containsKey(user.getUsername())) {
                error = getString(R.string.error_username_taken);
                return false;
            }

            return true;
        }
        /**
         * Actions to complete after execution
         * @param success    whether or not execution was successful
         */
        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                instance.addUser(user);

                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(intent);
                mEmailView.setText("");
                mUsernameView.setText("");
                mPasswordView.setText("");
            } else {
                if (error.equals(getString(R.string.error_email_taken))) {
                    mEmailView.setError(error);
                    mEmailView.requestFocus();
                } else if (error.equals(getString(R.string.error_username_taken))) {
                    mUsernameView.setError(error);
                    mUsernameView.requestFocus();
                }
            }
        }

        /**
         * Actions to execute if login is cancelled
         */
        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}


