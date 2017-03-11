package com.pendulab.theExchange.validator;

import java.lang.annotation.Annotation;

import eu.inmite.android.lib.validations.form.validators.BaseValidator;

/**
 * Created by Anh Ha Nguyen on 12/14/2015.
 */
public class ChangePasswordValidator extends BaseValidator<String[]> {

  @Override
  public boolean validate(Annotation annotation, String[] strings) {
    String newPass = strings[0];
    String confirmNewPass = strings[1];
    if (!newPass.equals(confirmNewPass)) {
      return false;
    }
    return true;
  }
}
