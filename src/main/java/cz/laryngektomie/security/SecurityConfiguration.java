package cz.laryngektomie.security;

import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter implements ApplicationContextAware {

    private final UserPrincipalDetailsService userPrincipalDetailsService;

    public SecurityConfiguration(UserPrincipalDetailsService userPrincipalDetailsService) {
        this.userPrincipalDetailsService = userPrincipalDetailsService;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().and().csrf().disable()
                .authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/poradna/**").authenticated()
                .antMatchers("/nastaveni/**").authenticated()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/admin/poradna/prispevky/**").hasRole("SPECIALIST")
                .antMatchers("/admin/poradna/temata/**").hasRole("SPECIALIST")
                .anyRequest().permitAll()
                .and()
                .formLogin()
                .loginPage("/prihlaseni").permitAll()
                .loginProcessingUrl("/prihlasit")
                .defaultSuccessUrl("/default", true)
                .failureUrl("/prihlaseni?error=true").permitAll()
                .and()
                .logout().logoutRequestMatcher(new AntPathRequestMatcher("/odhlaseni"))
                .deleteCookies("JSESSIONID").logoutSuccessUrl("/prihlaseni")
                .and()
                .rememberMe().tokenValiditySeconds(2592000);
    }

    @Bean
    DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(this.userPrincipalDetailsService);
        return daoAuthenticationProvider;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
