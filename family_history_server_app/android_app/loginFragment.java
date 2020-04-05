package com.codac.admin.familyhistoryapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.codac.admin.familyhistoryapp.aModel.aModel;

import java.net.URL;

import requests.eventRequest;
import requests.loginRequest;
import requests.personRequest;
import requests.registerRequest;
import results.eventResult;
import results.loginResult;
import results.personsResult;
import results.registerResult;
import serverProxy.proxy;


/**
 * Created by Admin on 3/15/17.
 */

public class loginFragment extends Fragment{

    aModel modl = new aModel();

    Button mSignInButton;
    Button mRegister;

    EditText mPassword;
    EditText mHost;
    EditText mPort;
    EditText mUsername;
    EditText mFirstName;
    EditText mLastName;
    EditText mEmail;


    String password;
    String host;
    String port;
    String username;
    String firstName;
    String lastName;
    String email;
    String gender;

    private RadioGroup mGenderRadio;
    private int checkedGenderId;
    private RadioButton mCheckedGender;

    private registerResult regResult;
    private loginResult logResult;
    private personsResult prsnsResult;
    private eventResult evntResult;
    private personsResult prsnResult;

    private String RpersonId;
    private String RauthToken;
    private String RfirstName;
    private String RlastName;

    private MainActivity main = null;



    public loginFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState){

        View v = inflater.inflate(R.layout.fragment_login, container, false);
        main = (MainActivity) getActivity();


        mSignInButton = (Button) v.findViewById(R.id.sign_in);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Signin", Toast.LENGTH_SHORT).show();

            }
        });

        mHost =  (EditText) v.findViewById(R.id.server_input);
        mPort =  (EditText) v.findViewById(R.id.port_input);
        mUsername =  (EditText) v.findViewById(R.id.username_input);
        mPassword =  (EditText) v.findViewById(R.id.password_input);
        mFirstName =  (EditText) v.findViewById(R.id.first_name_input);
        mLastName =  (EditText) v.findViewById(R.id.last_name_input);
        mEmail = (EditText) v.findViewById(R.id.email_input);

        mGenderRadio = (RadioGroup) v.findViewById(R.id.gender_radio);
        checkedGenderId =  mGenderRadio.getCheckedRadioButtonId();
        mCheckedGender = (RadioButton) v.findViewById(checkedGenderId);


        mRegister = (Button) v.findViewById(R.id.register);
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Register", Toast.LENGTH_SHORT).show();
                registerTask register = new registerTask();

                host = mHost.getText().toString();
                port = mPort.getText().toString();
                username = mUsername.getText().toString();
                password = mPassword.getText().toString();
                firstName = mFirstName.getText().toString();
                lastName = mLastName.getText().toString();
                email = mEmail.getText().toString();
                gender = mCheckedGender.getText().toString();

                register.execute();

            }
        });


        mSignInButton = (Button) v.findViewById(R.id.sign_in);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "SignIn", Toast.LENGTH_SHORT).show();
                signInTask signIn = new signInTask();

                host = mHost.getText().toString();
                port = mPort.getText().toString();
                username = mUsername.getText().toString();
                password = mPassword.getText().toString();

                signIn.execute();
            }
        });

        return v;

    }


    public class registerTask extends AsyncTask<URL, Integer, Boolean> {

        protected Boolean doInBackground(URL... urls) {

            proxy prxy = new proxy(host, port);
            registerRequest request = new registerRequest(username, password, email, firstName, lastName, gender);


            regResult = prxy.register(request);

            if(regResult == null) {
                return false;
            }
            else
                return true;
        }

        protected void onPostExecute(Boolean success) {
            if(success) {
                RpersonId = regResult.getPersonId();
                RauthToken = regResult.getAuthToken();
                if(RpersonId != null || RauthToken != null) {
                    syncTask sncTask = new syncTask();
                    sncTask.execute();
                }
                else Toast.makeText(getActivity(), regResult.getMessage(), Toast.LENGTH_LONG).show();
            }
            else
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
        }
    }

    public class signInTask extends AsyncTask<URL, Integer, Boolean> {

        protected Boolean doInBackground(URL... urls) {

            proxy prxy = new proxy(host, port);
            loginRequest request = new loginRequest(username, password);

            logResult = prxy.login(request);

            if(logResult == null) {
                return false;
            }
            else
                return true;
        }

        protected void onPostExecute(Boolean success) {
            if(success) {
                RpersonId = logResult.getPersonId();
                RauthToken = logResult.getAuthToken();
                if(RpersonId != null || RauthToken != null) {
                    syncTask sncTask = new syncTask();
                    sncTask.execute();
                }
                else Toast.makeText(getActivity(), logResult.getMessage(), Toast.LENGTH_LONG).show();
            }
            else Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();

        }
    }

    public class syncTask extends AsyncTask<URL, Integer, Boolean> {

        protected Boolean doInBackground(URL... urls) {

            proxy prxy = new proxy(host, port);

            personRequest pRequest = new personRequest(RpersonId, RauthToken);
            eventRequest eRequest = new eventRequest("", RauthToken);
            personRequest psRequest = new personRequest("", RauthToken);

            prsnResult = prxy.person(pRequest);
            prsnsResult = prxy.person(psRequest);
            evntResult = prxy.event(eRequest);

            if(prsnResult != null) {
                return true;
            }
            else
                return false;
        }

        protected void onPostExecute(Boolean success) {
            if(success) {
                Util util = new Util();
                util.setModelData(host, port, prsnResult, prsnsResult, evntResult, logResult, regResult);
                main.mapFragSwtich();
            }
            else
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_LONG).show();
        }
    }
}
