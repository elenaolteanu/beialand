package com.example.solomon;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.support.annotation.ColorInt;
import android.support.v4.view.ViewCompat;
import android.text.InputType;
import android.util.*;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.solomon.networkPackets.SignInData;
import com.example.solomon.networkPackets.SignUpData;
import com.example.solomon.runnables.SendAuthenticationDataRunnable;

import java.lang.reflect.Field;
import java.net.*;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

public class LoginActivity extends AppCompatActivity {

    public static Resources r;
    public static Context context;

    //Threads
    public static Thread connectClientThread;
    public static Thread manageClientConnectionThread;

    //networking variables
    public static Socket socket;
    public static ObjectOutputStream objectOutputStream;
    public static ObjectInputStream objectInputStream;

    //UI variables
    public static LinearLayout mainLinearLayout;
    public static LinearLayout loginLinearLayout;   //the linear layout that is common for both login and signup instances
    public static RadioButton loginRadioButton;
    public static RadioButton signupRadioButton;
    public static TextView titleTextView;
    public static int hintTextColor = Color.argb(50, 0, 0, 0);
    public static int orangeAccentColor = Color.argb(200,255, 161, 114);
    //sign in UI variables
    public static EditText usernameSignInEditText;
    public static EditText passwordSignInEditText;
    public static Button signInButton;
    //sign up UI variables
    public static EditText lastNameSignUpEditText;
    public static EditText firstNameSignUpEditText;
    public static EditText ageSignUpEditText;
    public static EditText usernameSignUpEditText;
    public static EditText passwordSignUpEditText;
    public static EditText passwordConfirmationSignUpEditText;
    public static Button signUpButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);




        //initialize variables
        r = getResources();
        context = this.getApplicationContext();



        //connect to servers
        connectToJavaServer();


        //initialize the UI
        initUI();

        //set login layout
        setLoginLayout();

        //login radio button listener
        loginRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                setLoginLayout();
            }
        });

        //sign up radio button listener
        signupRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                setSignUpLayout();
            }
        });


    }



    //SERVER METHODS
    public void connectToJavaServer()
    {
        connectClientThread = new Thread(new ConnectToJavaServerRunnable());

        connectClientThread.start();
    }

    //CLIENT COMMUNICATION METHODS
    public void sendSignInData()
    {
        String username = usernameSignInEditText.getText().toString();
        String password = passwordSignInEditText.getText().toString();
        usernameSignInEditText.setText("");
        passwordSignInEditText.setText("");
        SignInData signInData = new SignInData(username, password);
        Thread sendSignInDataThread = new Thread(new SendAuthenticationDataRunnable("sign in", signInData, objectOutputStream));
        sendSignInDataThread.start();
    }
    public void sendSignUpData()
    {
        String lastName = lastNameSignUpEditText.getText().toString();
        String firstName = firstNameSignUpEditText.getText().toString();
        int age = Integer.parseInt(ageSignUpEditText.getText().toString());
        String username = usernameSignUpEditText.getText().toString();
        String password = passwordSignUpEditText.getText().toString();
        String passwordConfirmation = passwordConfirmationSignUpEditText.getText().toString();
        lastNameSignUpEditText.setText("");
        firstNameSignUpEditText.setText("");
        ageSignUpEditText.setText("");
        usernameSignUpEditText.setText("");
        passwordSignUpEditText.setText("");
        passwordConfirmationSignUpEditText.setText("");
        SignUpData signUpData = new SignUpData(lastName, firstName, age, username, password, passwordConfirmation);
        Thread sendSignUpDataThread = new Thread(new SendAuthenticationDataRunnable("sign up", signUpData, objectOutputStream));
        sendSignUpDataThread.start();
    }




    //UI METHODS


    public void initUI()
    {
        mainLinearLayout = findViewById(R.id.MainMenuLinearLayout);
        loginLinearLayout = findViewById(R.id.CustomAutenthificationLinearLayout);
        loginRadioButton = findViewById(R.id.LoginRadioButton);
        signupRadioButton = findViewById(R.id.SignUpRadioButton);
        titleTextView = findViewById(R.id.TitleTextView);
        Drawable backround = ContextCompat.getDrawable(context, R.drawable.backround1);
        backround.setAlpha(180);
        mainLinearLayout.setBackground(backround);
    }
    public void setLoginLayout()
    {
        //uncheck the SignUpRadioButton if it's checked
        if (signupRadioButton.isChecked())
            signupRadioButton.setChecked(false);

        //remove all the views from the linear layout
        loginLinearLayout.removeAllViews();

        // UI dimensions
        float dip = 50f;

        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                r.getDisplayMetrics()
        );

        int width = (int)(3 * px);
        int height = (int) px;

        int loginButtonWidth = (int) (3 * px);
        int loginButtonHeight = (int) (px / 1.2f);

        //add login UI in the linear Layout


        LoginActivity.usernameSignInEditText = new EditText(LoginActivity.context);
        LinearLayout.LayoutParams layoutParamsUsernameEditText = new LinearLayout.LayoutParams(width , height);
        layoutParamsUsernameEditText.setMargins(0, 300, 0, 0);
        layoutParamsUsernameEditText.gravity = Gravity.CENTER;
        usernameSignInEditText.setLayoutParams(layoutParamsUsernameEditText);
        usernameSignInEditText.setHint("username");
        setCursorColor(usernameSignInEditText, orangeAccentColor);
        ColorStateList colorStateList = ColorStateList.valueOf(orangeAccentColor);
        ViewCompat.setBackgroundTintList(usernameSignInEditText, colorStateList);
        usernameSignInEditText.setHintTextColor(hintTextColor);


        LoginActivity.passwordSignInEditText = new EditText(LoginActivity.context);
        LinearLayout.LayoutParams layoutParamsPasswordEditText = new LinearLayout.LayoutParams(width , height);
        layoutParamsPasswordEditText.setMargins(0, 100, 0, 0);
        layoutParamsPasswordEditText.gravity = Gravity.CENTER;
        passwordSignInEditText.setLayoutParams(layoutParamsPasswordEditText);
        passwordSignInEditText.setHint("password");
        setCursorColor(passwordSignInEditText, orangeAccentColor);
        ViewCompat.setBackgroundTintList(passwordSignInEditText, colorStateList);
        passwordSignInEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordSignInEditText.setHintTextColor(hintTextColor);


        LoginActivity.signInButton = new Button(LoginActivity.context);
        LinearLayout.LayoutParams layoutParamsLoginButton = new LinearLayout.LayoutParams(loginButtonWidth, loginButtonHeight);
        layoutParamsLoginButton.gravity = Gravity.CENTER;
        layoutParamsLoginButton.setMargins(0, 100, 0, 0);
        signInButton.setLayoutParams(layoutParamsLoginButton);
        signInButton.setBackgroundColor(Color.argb(100, 255, 255, 255));
        signInButton.setText("Login");

        //sign in button listener
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSignInData();
            }
        });


        loginLinearLayout.addView(LoginActivity.usernameSignInEditText);
        loginLinearLayout.addView(LoginActivity.passwordSignInEditText);
        loginLinearLayout.addView(LoginActivity.signInButton);
    }
    public void setSignUpLayout()
    {
        //uncheck the LogInRadioButton if it's checked
        if (loginRadioButton.isChecked())
            loginRadioButton.setChecked(false);

        //remove all the views from the linear layout
        loginLinearLayout.removeAllViews();


        //UI dimensions
        float dip = 50f;

        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                r.getDisplayMetrics()
        );

        int width = (int)(3 * px);
        int height = (int) px;

        int signUpButtonWidth = (int) (3 * px);
        int signUpButtonHeight = (int) (px / 1.2f);


        //add login UI in the linear Layout


        TextView lastNameTextView = new TextView(LoginActivity.context);
        LinearLayout.LayoutParams layoutParamsLastNameTextView = new LinearLayout.LayoutParams(loginLinearLayout.getLayoutParams().MATCH_PARENT, loginLinearLayout.getLayoutParams().WRAP_CONTENT);
        layoutParamsLastNameTextView.setMargins(100, 100, 0, 0);
        lastNameTextView.setLayoutParams(layoutParamsLastNameTextView);
        lastNameTextView.setTextSize(15);
        lastNameTextView.setTextColor(Color.BLACK);
        lastNameTextView.setPadding(14, 10, 14, 14);
        lastNameTextView.setText("Last name: ");


        LoginActivity.lastNameSignUpEditText = new EditText(LoginActivity.context);
        LinearLayout.LayoutParams layoutParamsLastNameEditText = new LinearLayout.LayoutParams(width , height);
        layoutParamsLastNameEditText.setMargins(0, 10, 0, 0);
        layoutParamsLastNameEditText.gravity = Gravity.CENTER;
        lastNameSignUpEditText.setLayoutParams(layoutParamsLastNameEditText);
        lastNameSignUpEditText.setHint("enter last name");
        setCursorColor(lastNameSignUpEditText, orangeAccentColor);
        ColorStateList colorStateList = ColorStateList.valueOf(orangeAccentColor);
        ViewCompat.setBackgroundTintList(lastNameSignUpEditText, colorStateList);
        lastNameSignUpEditText.setHintTextColor(hintTextColor);


        TextView firstNameTextView = new TextView(LoginActivity.context);
        LinearLayout.LayoutParams layoutParamsFirstNameTextView = new LinearLayout.LayoutParams(loginLinearLayout.getLayoutParams().MATCH_PARENT, loginLinearLayout.getLayoutParams().WRAP_CONTENT);
        layoutParamsFirstNameTextView.setMargins(100, 10, 0, 0);
        firstNameTextView.setLayoutParams(layoutParamsFirstNameTextView);
        firstNameTextView.setTextSize(15);
        firstNameTextView.setTextColor(Color.BLACK);
        firstNameTextView.setPadding(14, 100, 14, 14);
        firstNameTextView.setText("First name: ");


        LoginActivity.firstNameSignUpEditText = new EditText(LoginActivity.context);
        LinearLayout.LayoutParams layoutParamsFirstNameEditText = new LinearLayout.LayoutParams(width , height);
        layoutParamsFirstNameEditText.setMargins(0, 10, 0, 0);
        layoutParamsFirstNameEditText.gravity = Gravity.CENTER;
        firstNameSignUpEditText.setLayoutParams(layoutParamsFirstNameEditText);
        firstNameSignUpEditText.setHint("enter first name");
        setCursorColor(firstNameSignUpEditText, orangeAccentColor);
        ViewCompat.setBackgroundTintList(firstNameSignUpEditText, colorStateList);
        firstNameSignUpEditText.setHintTextColor(hintTextColor);


        TextView ageTextView = new TextView(LoginActivity.context);
        LinearLayout.LayoutParams layoutParamsAgeTextView = new LinearLayout.LayoutParams(loginLinearLayout.getLayoutParams().MATCH_PARENT, loginLinearLayout.getLayoutParams().WRAP_CONTENT);
        layoutParamsAgeTextView.setMargins(100, 10, 0, 0);
        ageTextView.setLayoutParams(layoutParamsAgeTextView);
        ageTextView.setTextSize(15);
        ageTextView.setTextColor(Color.BLACK);
        ageTextView.setPadding(14, 100, 14, 14);
        ageTextView.setText("Age: ");


        LoginActivity.ageSignUpEditText = new EditText(LoginActivity.context);
        LinearLayout.LayoutParams layoutParamsAgeEditText = new LinearLayout.LayoutParams(width , height);
        layoutParamsAgeEditText.setMargins(0, 10, 0, 0);
        layoutParamsAgeEditText.gravity = Gravity.CENTER;
        ageSignUpEditText.setLayoutParams(layoutParamsAgeEditText);
        ageSignUpEditText.setHint("enter age");
        setCursorColor(ageSignUpEditText, orangeAccentColor);
        ViewCompat.setBackgroundTintList(ageSignUpEditText, colorStateList);
        ageSignUpEditText.setHintTextColor(hintTextColor);


        TextView usernameTextView = new TextView(LoginActivity.context);
        LinearLayout.LayoutParams layoutParamsUsernameTextView = new LinearLayout.LayoutParams(loginLinearLayout.getLayoutParams().MATCH_PARENT, loginLinearLayout.getLayoutParams().WRAP_CONTENT);
        layoutParamsUsernameTextView.setMargins(100, 10, 0, 0);
        usernameTextView.setLayoutParams(layoutParamsUsernameTextView);
        usernameTextView.setTextSize(15);
        usernameTextView.setTextColor(Color.BLACK);
        usernameTextView.setPadding(14, 100, 14, 14);
        usernameTextView.setText("Username: ");


        LoginActivity.usernameSignUpEditText = new EditText(LoginActivity.context);
        LinearLayout.LayoutParams layoutParamsUsernameEditText = new LinearLayout.LayoutParams(width , height);
        layoutParamsUsernameEditText.setMargins(0, 10, 0, 0);
        layoutParamsUsernameEditText.gravity = Gravity.CENTER;
        usernameSignUpEditText.setLayoutParams(layoutParamsUsernameEditText);
        usernameSignUpEditText.setHint("enter username");
        setCursorColor(usernameSignUpEditText, orangeAccentColor);
        ViewCompat.setBackgroundTintList(usernameSignUpEditText, colorStateList);
        usernameSignUpEditText.setHintTextColor(hintTextColor);


        TextView passwordTextView = new TextView(LoginActivity.context);
        LinearLayout.LayoutParams layoutParamsPasswordTextView = new LinearLayout.LayoutParams(loginLinearLayout.getLayoutParams().MATCH_PARENT, loginLinearLayout.getLayoutParams().WRAP_CONTENT);
        layoutParamsPasswordTextView.setMargins(100, 10, 0, 0);
        passwordTextView.setLayoutParams(layoutParamsPasswordTextView);
        passwordTextView.setTextSize(15);
        passwordTextView.setTextColor(Color.BLACK);
        passwordTextView.setPadding(14, 100, 14, 14);
        passwordTextView.setText("Password: ");


        LoginActivity.passwordSignUpEditText = new EditText(LoginActivity.context);
        LinearLayout.LayoutParams layoutParamsPasswordEditText = new LinearLayout.LayoutParams(width , height);
        layoutParamsPasswordEditText.setMargins(0, 10, 0, 0);
        layoutParamsPasswordEditText.gravity = Gravity.CENTER;
        passwordSignUpEditText.setLayoutParams(layoutParamsPasswordEditText);
        passwordSignUpEditText.setHint("enter password");
        setCursorColor(passwordSignUpEditText, orangeAccentColor);
        ViewCompat.setBackgroundTintList(passwordSignUpEditText, colorStateList);
        passwordSignUpEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordSignUpEditText.setHintTextColor(hintTextColor);



        TextView passwordConfirationTextView = new TextView(LoginActivity.context);
        LinearLayout.LayoutParams layoutParamsPasswordConfirmationTextView = new LinearLayout.LayoutParams(loginLinearLayout.getLayoutParams().MATCH_PARENT, loginLinearLayout.getLayoutParams().WRAP_CONTENT);
        layoutParamsPasswordConfirmationTextView.setMargins(100, 10, 0, 0);
        passwordConfirationTextView.setLayoutParams(layoutParamsPasswordConfirmationTextView);
        passwordConfirationTextView.setTextSize(15);
        passwordConfirationTextView.setTextColor(Color.BLACK);
        passwordConfirationTextView.setPadding(14, 100, 14, 14);
        passwordConfirationTextView.setText("Confirm password: ");


        LoginActivity.passwordConfirmationSignUpEditText = new EditText(LoginActivity.context);
        LinearLayout.LayoutParams layoutParamsPasswordConfirmationEditText = new LinearLayout.LayoutParams(width , height);
        layoutParamsPasswordConfirmationEditText.setMargins(0, 10, 0, 0);
        layoutParamsPasswordConfirmationEditText.gravity = Gravity.CENTER;
        passwordConfirmationSignUpEditText.setLayoutParams(layoutParamsPasswordConfirmationEditText);
        passwordConfirmationSignUpEditText.setHint("enter password");
        setCursorColor(passwordConfirmationSignUpEditText, orangeAccentColor);
        ViewCompat.setBackgroundTintList(passwordConfirmationSignUpEditText, colorStateList);
        passwordConfirmationSignUpEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordConfirmationSignUpEditText.setHintTextColor(hintTextColor);


        LoginActivity.signUpButton = new Button(LoginActivity.context);
        LinearLayout.LayoutParams layoutParamsSignUpButton = new LinearLayout.LayoutParams(signUpButtonWidth, signUpButtonHeight);
        layoutParamsSignUpButton.gravity = Gravity.CENTER;
        layoutParamsSignUpButton.setMargins(0, 100, 0, 100);
        signUpButton.setLayoutParams(layoutParamsSignUpButton);
        signUpButton.setBackgroundColor(Color.argb(100, 255, 255, 255));
        signUpButton.setText("Sign up");

        //sign up button listener
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSignUpData();
            }
        });


        loginLinearLayout.addView(lastNameTextView);
        loginLinearLayout.addView(lastNameSignUpEditText);
        loginLinearLayout.addView(firstNameTextView);
        loginLinearLayout.addView(firstNameSignUpEditText);
        loginLinearLayout.addView(ageTextView);
        loginLinearLayout.addView(ageSignUpEditText);
        loginLinearLayout.addView(usernameTextView);
        loginLinearLayout.addView(usernameSignUpEditText);
        loginLinearLayout.addView(passwordTextView);
        loginLinearLayout.addView(passwordSignUpEditText);
        loginLinearLayout.addView(passwordConfirationTextView);
        loginLinearLayout.addView(passwordConfirmationSignUpEditText);
        loginLinearLayout.addView(signUpButton);
    }



    //change cursor color
    public static void setCursorColor(EditText view, @ColorInt int color) {
        try {
            // Get the cursor resource id
            Field field = TextView.class.getDeclaredField("mCursorDrawableRes");
            field.setAccessible(true);
            int drawableResId = field.getInt(view);

            // Get the editor
            field = TextView.class.getDeclaredField("mEditor");
            field.setAccessible(true);
            Object editor = field.get(view);

            // Get the drawable and set a color filter
            Drawable drawable = ContextCompat.getDrawable(view.getContext(), drawableResId);
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            Drawable[] drawables = {drawable, drawable};

            // Set the drawables
            field = editor.getClass().getDeclaredField("mCursorDrawable");
            field.setAccessible(true);
            field.set(editor, drawables);
        } catch (Exception ignored) {
        }
    }
}