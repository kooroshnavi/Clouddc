package com.navi.dcim.otp;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class OtpForm {

    private char d1;
    private char d2;
    private char d3;
    private char d4;
    private char d5;
    private char d6;

    private String address;

 public String getAddress() {
  return address;
 }

 public void setAddress(String address) {
  this.address = address;
 }

 public char getD1() {
  return d1;
 }

 public void setD1(char d1) {
  this.d1 = d1;
 }

 public char getD2() {
  return d2;
 }

 public void setD2(char d2) {
  this.d2 = d2;
 }

 public char getD3() {
  return d3;
 }

 public void setD3(char d3) {
  this.d3 = d3;
 }

 public char getD4() {
  return d4;
 }

 public void setD4(char d4) {
  this.d4 = d4;
 }

 public char getD5() {
  return d5;
 }

 public void setD5(char d5) {
  this.d5 = d5;
 }

 public char getD6() {
  return d6;
 }

 public void setD6(char d6) {
  this.d6 = d6;
 }
}
