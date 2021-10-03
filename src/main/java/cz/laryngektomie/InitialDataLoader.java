package cz.laryngektomie;

import cz.laryngektomie.helper.Const;
import cz.laryngektomie.model.forum.Category;
import cz.laryngektomie.model.forum.Topic;
import cz.laryngektomie.model.news.News;
import cz.laryngektomie.model.news.NewsType;
import cz.laryngektomie.model.security.User;
import cz.laryngektomie.repository.news.NewsTypeRepository;
import cz.laryngektomie.repository.security.UserRepository;
import cz.laryngektomie.service.forum.CategoryService;
import cz.laryngektomie.service.forum.TopicService;
import cz.laryngektomie.service.news.NewsService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class InitialDataLoader implements ApplicationRunner {

    boolean alreadySetup = false;

    private final UserRepository userRepository;
    private final CategoryService categoryService;
    private final NewsService newsService;
    private final TopicService topicService;
    private final NewsTypeRepository newsTypeRepository;

    public InitialDataLoader(UserRepository userRepository, CategoryService categoryService, NewsService newsService, TopicService topicService, NewsTypeRepository newsTypeRepository) {
        this.userRepository = userRepository;
        this.categoryService = categoryService;
        this.newsService = newsService;
        this.topicService = topicService;
        this.newsTypeRepository = newsTypeRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        if (alreadySetup)
            return;

        //Přidání admina
        if (userRepository.findByUsername("admin") == null) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setFirstName("Vitezslav");
            admin.setLastName("Kanok");
            admin.setPassword(bCryptPasswordEncoder.encode("admin"));
            admin.setEmail("vitezslav.kanok@email.cz");
            admin.setRole(Const.ROLE_ADMIN);
            admin.setEnabled(true);
            admin.setAboutMe("Jmenuji se Hana a jsem dva roky po operaci, kdy mi byl odebrán hrtan a hlasivky. Myslela jsem, že už nebudu nikdy mluvit, jen psát. Cítila jsem beznaděj, ale když jsem se dozvěděla o možnosti jícnového hlasu, okamžitě jsem se rozhodla. Řekla jsem si, že jsem se v životě naučila hodně věcí, a proto se naučím i toto. Dnes mluvím i telefonuji.");
            admin.setAboutUs(false);
            userRepository.save(admin);

            //vytvoreni kategorii
            String[] categoryNames = new String[]{"Pravidla", "Laryngektomie", "Jícnový hlas", "Hlasová protéza", "Elektrolarynx"};
            for (String categoryName : categoryNames) {
                Category category = new Category();
                category.setName(categoryName);
                category.setUser(admin);
                categoryService.saveOrUpdate(category);
            }

            //vytvoření příspěvků
            String[] topicNames = new String[]{"Vítejte v naší poradně", "Základní info o laryngektomii"};
            for (String topicName : topicNames) {
                Topic topic = new Topic();
                topic.setCategory(categoryService.findByName("Pravidla").get());
                topic.setName(topicName);
                topic.setText(topicName);
                topic.setUser(admin);
                topicService.saveOrUpdate(topic);
            }

        }

        if (userRepository.findByUsername("doktor") == null) {
            //Pridani specialisty/doktora
            User specialist = new User();
            specialist.setUsername("doktor");
            specialist.setFirstName("doktor");
            specialist.setLastName("doktor");
            specialist.setPassword(bCryptPasswordEncoder.encode("doktor"));
            specialist.setEmail("doktor@test.com");
            specialist.setRole(Const.ROLE_SPECIALISTS);
            specialist.setEnabled(true);
            specialist.setAboutUs(false);

            specialist.setAboutMe("Jmenuji se Jindřich a když jsem viděl jak lze mluvit po operaci i bez hrtanu, ihned jsem se rozhodl. Pravidelně trénuji jícnový hlas, ale zatím mi ještě nejde tak plynule a automaticky, jak bych si přál, proto používám elektrolarynx, se kterým je pro mě komunikace snadnější. Cvičení jícnového hlasu nevzdávám a věřím, že když se jej naučili jiní, dokážu to taky!");
            userRepository.save(specialist);
        }
        //Pridani uživatele
        if (userRepository.findByUsername("user") == null) {
            User user = new User();
            user.setUsername("user");
            user.setFirstName("user");
            user.setLastName("user");
            user.setPassword(bCryptPasswordEncoder.encode("user"));
            user.setEmail("user@test.com");
            user.setRole(Const.ROLE_USER);
            user.setEnabled(true);
            user.setAboutUs(false);
            user.setAboutMe("Jmenuji se Roman a jsem čtyři roky po tracheostomické operaci. Rozhodl jsem se pro jícnový hlas, po necelém roce jsem začal znovu komunikovat. Vrátil jsem se na čas do zaměstnání. Nyní mluvím, telefonuji, žiju aktivní život, na nemoci nemám čas :-). Věřte, že i když jsou začátky těžké, výsledek se dostaví! Nenechte se odradit, chce to jen \"kecat a kecat\"!");
            userRepository.save(user);
        }

        //Pridani uzivatelu do o-nas

        //Pridani uživatele EVA
        if (userRepository.findByUsername("Eva") == null) {
            User user = new User();
            user.setUsername("Eva");

            

            /*byte[] array = Files.readAllBytes(Paths.get(ResourceUtils.getFile("static/images/clenove/eva.jpg").getPath()));
            Image image = new Image(user.getUsername(), "image/jpeg", array);
            user.setImage(image);*/

            user.setFirstName("Eva");
            user.setLastName("Eva");
            user.setPassword(bCryptPasswordEncoder.encode("Eva"));
            user.setEmail("Eva@test.com");
            user.setRole(Const.ROLE_USER);
            user.setEnabled(true);
            user.setAboutUs(true);
            user.setAboutMe("Jmenuji se Eva a pracuji jako klinická logopedka. Mezi mými klienty jsou také pacienti se ztrátou hlasu po totální laryngektomii. Prošli si náročnou hlasovou rehabilitací a nyní opět úspěšně komunikují. Domnívám se, že tito úspěšní pacienti, jsou nejlepšími vzory a motivací pro ostatní.");
            userRepository.save(user);
        }

        //Pridani uživatele Roman
        if (userRepository.findByUsername("Roman") == null) {
            User user = new User();
            user.setUsername("Roman");

            /*byte[] array = Files.readAllBytes(Paths.get(ResourceUtils.getFile("classpath:static/images/clenove/roman.jpg").getPath()));
            Image image = new Image(user.getUsername(), "image/jpeg", array);
            user.setImage(image);*/

            user.setFirstName("Roman");
            user.setLastName("Roman");
            user.setPassword(bCryptPasswordEncoder.encode("Roman"));
            user.setEmail("Roman@test.com");
            user.setRole(Const.ROLE_USER);
            user.setEnabled(true);
            user.setAboutUs(true);
            user.setAboutMe("Jmenuji se Roman a jsem pět let po tracheostomické operaci. Rozhodl jsem se pro jícnový hlas, po necelém roce jsem začal znovu komunikovat. Vrátil jsem se na čas do zaměstnání. Nyní mluvím, telefonuji, žiju aktivní život, na nemoci nemám čas :-). Věřte, že i když jsou začátky těžké, výsledek se dostaví! Nenechte se odradit, chce to jen \"kecat a kecat\"!");
            userRepository.save(user);
        }

        //Pridani uživatele Hana
        if (userRepository.findByUsername("Hana") == null) {
            User user = new User();
            user.setUsername("Hana");

            /*byte[] array = Files.readAllBytes(Paths.get(ResourceUtils.getFile("classpath:static/images/clenove/hana.jpg").getPath()));
            Image image = new Image(user.getUsername(), "image/jpeg", array);
            user.setImage(image);*/

            user.setFirstName("Hana");
            user.setLastName("Hana");
            user.setPassword(bCryptPasswordEncoder.encode("Hana"));
            user.setEmail("Hana@test.com");
            user.setRole(Const.ROLE_USER);
            user.setEnabled(true);
            user.setAboutUs(true);
            user.setAboutMe("Jmenuji se Hana a jsem od roku 2016 po operaci, kdy mi byl odebrán hrtan a hlasivky. Myslela jsem, že už nebudu nikdy mluvit, jen psát. Cítila jsem beznaděj, ale když jsem se dozvěděla o možnosti jícnového hlasu, okamžitě jsem se rozhodla. Řekla jsem si, že jsem se v životě naučila hodně věcí, a proto se naučím i toto. Dnes mluvím i telefonuji.");
            userRepository.save(user);
        }

        //Pridani uživatele Zdenek
        if (userRepository.findByUsername("Zdeněk") == null) {
            User user = new User();
            user.setUsername("Zdeněk");

            /*byte[] array = Files.readAllBytes(Paths.get(ResourceUtils.getFile("classpath:static/images/clenove/zdenek.jpg").getPath()));
            Image image = new Image(user.getUsername(), "image/jpeg", array);
            user.setImage(image);*/

            user.setFirstName("Zdeněk");
            user.setLastName("Zdeněk");
            user.setPassword(bCryptPasswordEncoder.encode("Zdeněk"));
            user.setEmail("Zdeněk@test.com");
            user.setRole(Const.ROLE_USER);
            user.setEnabled(true);
            user.setAboutUs(true);
            user.setAboutMe("Jmenuji se Zdeněk a jsem po operaci hrtanu již pět let. Po operaci jsem se cítil strašně. Když jsem ale viděl nahrávky, že se dá mluvit i jinak než s hlasivkami, tedy jícnem, řekl jsem si, že když to dokázali jiní, dokážu to taky. Dnes se domluvím po celé Evropě, telefonuji. Znovu jsem se oženil, vedu spokojený život. Co bych vzkázal ostatním? Jícnový hlas je zpočátku námaha, ale stojí to za to!");
            userRepository.save(user);
        }

        //Pridani uživatele Míša
        if (userRepository.findByUsername("Míša") == null) {
            User user = new User();
            user.setUsername("Míša");

            /*byte[] array = Files.readAllBytes(Paths.get(ResourceUtils.getFile("classpath:static/images/clenove/misa.jpg").getPath()));
            Image image = new Image(user.getUsername(), "image/jpeg", array);
            user.setImage(image);*/

            user.setFirstName("Míša");
            user.setLastName("Míša");
            user.setPassword(bCryptPasswordEncoder.encode("Míša"));
            user.setEmail("Míša@test.com");
            user.setRole(Const.ROLE_USER);
            user.setEnabled(true);
            user.setAboutUs(true);
            user.setAboutMe("Jmenuji se Míša a první rok po operaci jsem se snažil naučit jícnový hlas, což se mi nedařilo. Poté mi byla voperována hlasová protéza, ale ani ta se mi neosvědčila. Nyní, třetí rok po operaci, používám elektrolarynx, který mi vyhovuje. Lituji, že jsem nezačal používat přístroj hned po operaci. Domluvím se všude, např.: v bance, na poště, aj. S lidmi, kteří mě znají i telefonuji.");
            userRepository.save(user);
        }

        //Pridani uživatele Jindřich
        if (userRepository.findByUsername("Jindřich") == null) {
            User user = new User();
            user.setUsername("Jindřich");

            /*byte[] array = Files.readAllBytes(Paths.get(ResourceUtils.getFile("classpath:static/images/clenove/jindrich.jpg").getPath()));
            Image image = new Image(user.getUsername(), "image/jpeg", array);
            user.setImage(image);
*/
            user.setFirstName("Jindřich");
            user.setLastName("Jindřich");
            user.setPassword(bCryptPasswordEncoder.encode("Jindřich"));
            user.setEmail("Jindřich@test.com");
            user.setRole(Const.ROLE_USER);
            user.setEnabled(true);
            user.setAboutUs(true);
            user.setAboutMe("Jmenuji se Jindřich a když jsem viděl jak lze mluvit po operaci i bez hrtanu, ihned jsem se rozhodl. Pravidelně trénuji jícnový hlas, ale zatím mi ještě nejde tak plynule a automaticky, jak bych si přál, proto používám elektrolarynx, se kterým je pro mě komunikace snadnější. Cvičení jícnového hlasu nevzdávám a věřím, že když se jej naučili jiní, dokážu to taky!");
            userRepository.save(user);
        }

        //Pridani uživatele Karel
        if (userRepository.findByUsername("Karel") == null) {
            User user = new User();
            user.setUsername("Karel");

            /*byte[] array = Files.readAllBytes(Paths.get(ResourceUtils.getFile("classpath:static/images/clenove/karel.jpg").getPath()));
            Image image = new Image(user.getUsername(), "image/jpeg", array);
            user.setImage(image);*/

            user.setFirstName("Karel");
            user.setLastName("Karel");
            user.setPassword(bCryptPasswordEncoder.encode("Karel"));
            user.setEmail("Karel@test.com");
            user.setRole(Const.ROLE_USER);
            user.setEnabled(true);
            user.setAboutUs(true);
            user.setAboutMe("Jmenuji se Karel a laryngektomie byla pro mě neznámým pojmem, než jsem se s ní před pěti lety \\\"potkal\\\". Operací a léčbou si prošla moje manželka a já najednou viděl celou problematiku zblízka - statečnost své ženy, um a obětavost zdravotníků. Viděl jsem také nedostatky, např. v nízké informovanosti pacientů i veřejnosti. Uvítal jsem vznik spolku, snažím se pomáhat a jako bývalý novinář i přes média. Těší mě vydávání Zpravodaje a věřím, že má naše práce smysl.");
            userRepository.save(user);
        }

        String[] typeOfNews = new String[]{"setkání", "konference", "zpravodaj", "oznámení", "terapie", "sezení"};

        for (String type : typeOfNews) {
            //pridani typu novinky
            if (!newsTypeRepository.findByName(type).isPresent()) {
                NewsType newsType = new NewsType();
                newsType.setName(type);
                newsTypeRepository.save(newsType);
            }
        }


        //Novinky inicializace
        String zpravodajName = "Vydáváme zpravodaj";
        User newsAdmin = userRepository.findByUsername("admin");
        if (!newsService.findByName(zpravodajName).isPresent()) {
            News news = new News();
            news.setUser(newsAdmin);

           /* byte[] array = Files.readAllBytes(Paths.get(ResourceUtils.getFile("classpath:static/images/init/zpravodaj.jpg").getPath()));
            Image image = new Image("Zpravodaj", "image/jpeg", array);
            Collection<Image> images = new ArrayList<>();
            images.add(image);
            news.setImages(images);*/

            news.setNewsType(newsTypeRepository.findByName("zpravodaj").get());

            news.setName(zpravodajName);
            news.setText("<p><span style=\"color: rgb(85, 85, 85);\"><b>V posledním listopadovém týdnu vyšlo druhé číslo Zpravodaje a měli byste ho již mít doma.</b></span><br></p><p style=\"\">z obsahu vyjímáme:</p><ul><li>na co nebyl v ordinaci čas:<br></li></ul><ul><li>odpovědi na otázky stran výživy</li></ul><ul><li>výsledky ankety</li></ul><ul><li>Jak se nám žije po laryngektomii:&nbsp;<span style=\"color: rgb(51, 51, 51);\">příběh pana Zdeňka</span></li></ul><ul><li>reportáž z našeho 2. setkání v Pardubicích<br></li></ul><ul><li>podrobnosti o aplikaci záchranka<br></li></ul><ul><li>a další zajímavosti a rady</li></ul>");

            newsService.saveOrUpdate(news);
        }

        String setkani1 = "Dne 9. 1. 2020 proběhlo povánoční tříkrálové posezení.";
        if (!newsService.findByName(setkani1).isPresent()) {
            News news = new News();
            news.setUser(newsAdmin);

            /*Collection<Image> images = new ArrayList<>();
            byte[] array = Files.readAllBytes(Paths.get(ResourceUtils.getFile("classpath:static/images/init/a.jpg").getPath()));
            byte[] array1 = Files.readAllBytes(Paths.get(ResourceUtils.getFile("classpath:static/images/init/b.jpg").getPath()));
            Image image = new Image("Setkani1", "image/jpeg", array);
            Image image1 = new Image("Setkana2", "image/jpeg", array1);
            images.add(image);
            images.add(image1);
            news.setImages(images);*/

            news.setNewsType(newsTypeRepository.findByName("terapie").get());

            news.setName(setkani1);
            news.setText("<p style=\"margin-left: 0px;\"><font>Tentokrát jsme se sešli v komornějším počtu, v příjemném prostředí restaurace Fascila. Pozvání na besedu přijal&nbsp;MUDr. Milan Vošmik, Ph.D.,&nbsp;zástupce přednosty Onkologické kliniky ve FN HK, předseda Sekce pro nádory hlavy a krku v rámci České onkologické společnosti, který nám zodpověděl mnoho, pro nás zajímavých, otázek.</font></p><p style=\"margin-left: 0px;\"><font>Klinická logopedka&nbsp;Mgr. Jarmila Hofmanová&nbsp;nás&nbsp;seznámila se svými zkušenostmi s použitím přípravku <b>ENZYMEL</b>, s jeho účinky, použitím i s novou formou nejen gelu a zubní pasty, ale také ústních pastilek.<br></font></p><p style=\"margin-left: 0px;\"><font>Všichni účastníci vyjadřovali nadšení a uznání k vysoké odbornosti našich hostů a k jejich laskavému přístupu. Nejen, že jsme se dozvěděli spoutu nových informací, také jsme se zasmáli a odnášeli si mnoho témat k přemýšlení.<br></font></p><p style=\"margin-left: 0px;\"><font>Všichni si vážíme ochoty a času našich hostí, který nám věnovali a ještě jednou jim&nbsp;DĚKUJEME!</font></p>");

            newsService.saveOrUpdate(news);
        }

        String setkani2 = "Ve čtvrtek 5. 12. 2019 jsme se sešli na společné logopedické terapii.";
        if (!newsService.findByName(setkani2).isPresent()) {
            News news = new News();
            news.setUser(newsAdmin);

            /*Collection<Image> images = new ArrayList<>();
            byte[] array = Files.readAllBytes(Paths.get(ResourceUtils.getFile("classpath:static/images/init/c.jpg").getPath()));
            byte[] array1 = Files.readAllBytes(Paths.get(ResourceUtils.getFile("classpath:static/images/init/d.jpg").getPath()));
            Image image = new Image("Setkani1", "image/jpeg", array);
            Image image1 = new Image("Setkana2", "image/jpeg", array1);
            images.add(image);
            images.add(image1);
            news.setImages(images);*/

            news.setNewsType(newsTypeRepository.findByName("setkání").get());

            news.setName(setkani2);
            news.setText("<p style=\"margin-left: 0px;\"><font>Ve čtvrtek&nbsp;<font>5. 12. 2019&nbsp;</font>jsme se sešli na společné logopedické terapii. Kromě toho, že to bylo velice příjemné setkání, také jsme pracovali i předávali své zkušenosti.<br></font></p><p style=\"margin-left: 0px;\"><font>S velmi zajímavými \"vychytávkami\" ohledně kanyly a jejího krytí a dalšího nás seznamuje pan&nbsp;Jindřich Habart,&nbsp;Jindro díky :)!</font></p><p style=\"margin-left: 0px;\"><font>Poděkování patří též manželům&nbsp;Láďovi a Ivaně Plačkovým&nbsp;- přivezli \"přebytky\" kanyl pro odsávání. Tak pokud byste někdo potřebovali, jsou u mě k dispozici</font>.</p>");
            newsService.saveOrUpdate(news);
        }


        alreadySetup = true;
    }
}
