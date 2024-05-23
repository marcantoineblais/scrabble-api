package com.marcblais.scrabbleapi.dto;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pattern {
   String regex;
   String bonus;

   @Override
   public String toString() {
      return "Pattern{" +
              "regex='" + regex + '\'' +
              ", bonus='" + bonus + '\'' +
              '}';
   }

   @Override
   public final boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Pattern pattern))
         return false;

      return regex.equals(pattern.regex);
   }

   @Override
   public int hashCode() {
      return regex.hashCode();
   }
}
