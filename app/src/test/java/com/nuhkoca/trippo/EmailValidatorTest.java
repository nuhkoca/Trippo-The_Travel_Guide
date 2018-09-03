package com.nuhkoca.trippo;

import android.content.Context;
import android.content.res.Resources;

import com.nuhkoca.trippo.util.test.EmailValidator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class EmailValidatorTest {

    private static final String EMAIL_ADDRESS = "nuhkocaa@gmail.com";

    @Mock
    private Context fakeContext;
    @Mock
    private Resources fakeRes;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(fakeContext.getResources()).thenReturn(fakeRes);
        when(fakeRes.getString(R.string.mail_address)).thenReturn(EMAIL_ADDRESS);
    }

    @Test
    public void emailValidator_CorrectEmail_ReturnsTrue() {
        assertThat(EmailValidator.isValidEmail(fakeRes.getString(R.string.mail_address)), is(true));
    }
}