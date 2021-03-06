package cz.laryngektomie;

import com.github.javafaker.Faker;
import cz.laryngektomie.model.article.Article;
import cz.laryngektomie.model.article.ArticleType;
import cz.laryngektomie.model.forum.Category;
import cz.laryngektomie.model.forum.Post;
import cz.laryngektomie.model.forum.Topic;
import cz.laryngektomie.model.security.User;
import cz.laryngektomie.model.security.UserRole;
import cz.laryngektomie.repository.jpa.article.ArticleTypeRepository;
import cz.laryngektomie.repository.jpa.security.UserRepository;
import cz.laryngektomie.service.article.ArticleService;
import cz.laryngektomie.service.forum.CategoryService;
import cz.laryngektomie.service.forum.PostService;
import cz.laryngektomie.service.forum.TopicService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
public class InitialDataLoader implements ApplicationRunner {

    private final UserRepository userRepository;
    private final CategoryService categoryService;
    private final ArticleService articleService;
    private final TopicService topicService;
    private final ArticleTypeRepository articleTypeRepository;
    private final PostService postService;
    boolean alreadySetup = false;

    public InitialDataLoader(UserRepository userRepository, CategoryService categoryService, ArticleService articleService, TopicService topicService, ArticleTypeRepository articleTypeRepository, PostService postService) {
        this.userRepository = userRepository;
        this.categoryService = categoryService;
        this.articleService = articleService;
        this.topicService = topicService;
        this.articleTypeRepository = articleTypeRepository;
        this.postService = postService;
    }

    @Override
    public void run(ApplicationArguments args) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        if (alreadySetup)
            return;

        //P??id??n?? admina
        if (userRepository.findByUsername("admin") == null) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setFirstName("Vitezslav");
            admin.setLastName("Kanok");
            admin.setPassword(bCryptPasswordEncoder.encode("admin"));
            admin.setEmail("vitezslav.kanok@email.cz");
            admin.setRole(UserRole.ADMIN);
            admin.setEnabled(true);
            admin.setAboutMe("Jmenuji se Hana a jsem dva roky po operaci, kdy mi byl odebr??n hrtan a hlasivky. Myslela jsem, ??e u?? nebudu nikdy mluvit, jen ps??t. C??tila jsem beznad??j, ale kdy?? jsem se dozv??d??la o mo??nosti j??cnov??ho hlasu, okam??it?? jsem se rozhodla. ??ekla jsem si, ??e jsem se v ??ivot?? nau??ila hodn?? v??c??, a proto se nau????m i toto. Dnes mluv??m i telefonuji.");
            admin.setAboutUs(false);
            userRepository.save(admin);

            //vytvoreni kategorii
            String[] categoryNames = new String[]{"Pravidla", "Laryngektomie", "J??cnov?? hlas", "Hlasov?? prot??za", "Elektrolarynx"};
            for (String categoryName : categoryNames) {
                Category category = new Category();
                category.setName(categoryName);
                category.setUser(admin);
                categoryService.saveOrUpdate(category);
            }

            //vytvo??en?? p????sp??vk??
            String[] topicNames = new String[]{"V??tejte v na???? poradn??", "Z??kladn?? info o laryngektomii"};
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
            specialist.setRole(UserRole.SPECIALISTS);
            specialist.setEnabled(true);
            specialist.setAboutUs(false);

            specialist.setAboutMe("Jmenuji se Jind??ich a kdy?? jsem vid??l jak lze mluvit po operaci i bez hrtanu, ihned jsem se rozhodl. Pravideln?? tr??nuji j??cnov?? hlas, ale zat??m mi je??t?? nejde tak plynule a automaticky, jak bych si p????l, proto pou????v??m elektrolarynx, se kter??m je pro m?? komunikace snadn??j????. Cvi??en?? j??cnov??ho hlasu nevzd??v??m a v??????m, ??e kdy?? se jej nau??ili jin??, dok????u to taky!");
            userRepository.save(specialist);
        }
        //Pridani u??ivatele
        if (userRepository.findByUsername("user") == null) {
            User user = new User();
            user.setUsername("user");
            user.setFirstName("user");
            user.setLastName("user");
            user.setPassword(bCryptPasswordEncoder.encode("user"));
            user.setEmail("user@test.com");
            user.setRole(UserRole.USER);
            user.setEnabled(true);
            user.setAboutUs(false);
            user.setAboutMe("Jmenuji se Roman a jsem ??ty??i roky po tracheostomick?? operaci. Rozhodl jsem se pro j??cnov?? hlas, po necel??m roce jsem za??al znovu komunikovat. Vr??til jsem se na ??as do zam??stn??n??. Nyn?? mluv??m, telefonuji, ??iju aktivn?? ??ivot, na nemoci nem??m ??as :-). V????te, ??e i kdy?? jsou za????tky t????k??, v??sledek se dostav??! Nenechte se odradit, chce to jen \"kecat a kecat\"!");
            userRepository.save(user);
        }

        //Pridani uzivatelu do o-nas

        //Pridani u??ivatele EVA
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
            user.setRole(UserRole.USER);
            user.setEnabled(true);
            user.setAboutUs(true);
            user.setAboutMe("Jmenuji se Eva a pracuji jako klinick?? logopedka. Mezi m??mi klienty jsou tak?? pacienti se ztr??tou hlasu po tot??ln?? laryngektomii. Pro??li si n??ro??nou hlasovou rehabilitac?? a nyn?? op??t ??sp????n?? komunikuj??. Domn??v??m se, ??e tito ??sp????n?? pacienti, jsou nejlep????mi vzory a motivac?? pro ostatn??.");
            userRepository.save(user);
        }

        //Pridani u??ivatele Roman
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
            user.setRole(UserRole.USER);
            user.setEnabled(true);
            user.setAboutUs(true);
            user.setAboutMe("Jmenuji se Roman a jsem p??t let po tracheostomick?? operaci. Rozhodl jsem se pro j??cnov?? hlas, po necel??m roce jsem za??al znovu komunikovat. Vr??til jsem se na ??as do zam??stn??n??. Nyn?? mluv??m, telefonuji, ??iju aktivn?? ??ivot, na nemoci nem??m ??as :-). V????te, ??e i kdy?? jsou za????tky t????k??, v??sledek se dostav??! Nenechte se odradit, chce to jen \"kecat a kecat\"!");
            userRepository.save(user);
        }

        //Pridani u??ivatele Hana
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
            user.setRole(UserRole.USER);
            user.setEnabled(true);
            user.setAboutUs(true);
            user.setAboutMe("Jmenuji se Hana a jsem od roku 2016 po operaci, kdy mi byl odebr??n hrtan a hlasivky. Myslela jsem, ??e u?? nebudu nikdy mluvit, jen ps??t. C??tila jsem beznad??j, ale kdy?? jsem se dozv??d??la o mo??nosti j??cnov??ho hlasu, okam??it?? jsem se rozhodla. ??ekla jsem si, ??e jsem se v ??ivot?? nau??ila hodn?? v??c??, a proto se nau????m i toto. Dnes mluv??m i telefonuji.");
            userRepository.save(user);
        }

        //Pridani u??ivatele Zdenek
        if (userRepository.findByUsername("Zden??k") == null) {
            User user = new User();
            user.setUsername("Zden??k");

            /*byte[] array = Files.readAllBytes(Paths.get(ResourceUtils.getFile("classpath:static/images/clenove/zdenek.jpg").getPath()));
            Image image = new Image(user.getUsername(), "image/jpeg", array);
            user.setImage(image);*/

            user.setFirstName("Zden??k");
            user.setLastName("Zden??k");
            user.setPassword(bCryptPasswordEncoder.encode("Zden??k"));
            user.setEmail("Zden??k@test.com");
            user.setRole(UserRole.USER);
            user.setEnabled(true);
            user.setAboutUs(true);
            user.setAboutMe("Jmenuji se Zden??k a jsem po operaci hrtanu ji?? p??t let. Po operaci jsem se c??til stra??n??. Kdy?? jsem ale vid??l nahr??vky, ??e se d?? mluvit i jinak ne?? s hlasivkami, tedy j??cnem, ??ekl jsem si, ??e kdy?? to dok??zali jin??, dok????u to taky. Dnes se domluv??m po cel?? Evrop??, telefonuji. Znovu jsem se o??enil, vedu spokojen?? ??ivot. Co bych vzk??zal ostatn??m? J??cnov?? hlas je zpo????tku n??maha, ale stoj?? to za to!");
            userRepository.save(user);
        }

        //Pridani u??ivatele M????a
        if (userRepository.findByUsername("M????a") == null) {
            User user = new User();
            user.setUsername("M????a");

            /*byte[] array = Files.readAllBytes(Paths.get(ResourceUtils.getFile("classpath:static/images/clenove/misa.jpg").getPath()));
            Image image = new Image(user.getUsername(), "image/jpeg", array);
            user.setImage(image);*/

            user.setFirstName("M????a");
            user.setLastName("M????a");
            user.setPassword(bCryptPasswordEncoder.encode("M????a"));
            user.setEmail("M????a@test.com");
            user.setRole(UserRole.USER);
            user.setEnabled(true);
            user.setAboutUs(true);
            user.setAboutMe("Jmenuji se M????a a prvn?? rok po operaci jsem se sna??il nau??it j??cnov?? hlas, co?? se mi neda??ilo. Pot?? mi byla voperov??na hlasov?? prot??za, ale ani ta se mi neosv??d??ila. Nyn??, t??et?? rok po operaci, pou????v??m elektrolarynx, kter?? mi vyhovuje. Lituji, ??e jsem neza??al pou????vat p????stroj hned po operaci. Domluv??m se v??ude, nap??.: v bance, na po??t??, aj. S lidmi, kte???? m?? znaj?? i telefonuji.");
            userRepository.save(user);
        }

        //Pridani u??ivatele Jind??ich
        if (userRepository.findByUsername("Jind??ich") == null) {
            User user = new User();
            user.setUsername("Jind??ich");

            /*byte[] array = Files.readAllBytes(Paths.get(ResourceUtils.getFile("classpath:static/images/clenove/jindrich.jpg").getPath()));
            Image image = new Image(user.getUsername(), "image/jpeg", array);
            user.setImage(image);
*/
            user.setFirstName("Jind??ich");
            user.setLastName("Jind??ich");
            user.setPassword(bCryptPasswordEncoder.encode("Jind??ich"));
            user.setEmail("Jind??ich@test.com");
            user.setRole(UserRole.USER);
            user.setEnabled(true);
            user.setAboutUs(true);
            user.setAboutMe("Jmenuji se Jind??ich a kdy?? jsem vid??l jak lze mluvit po operaci i bez hrtanu, ihned jsem se rozhodl. Pravideln?? tr??nuji j??cnov?? hlas, ale zat??m mi je??t?? nejde tak plynule a automaticky, jak bych si p????l, proto pou????v??m elektrolarynx, se kter??m je pro m?? komunikace snadn??j????. Cvi??en?? j??cnov??ho hlasu nevzd??v??m a v??????m, ??e kdy?? se jej nau??ili jin??, dok????u to taky!");
            userRepository.save(user);
        }

        //Pridani u??ivatele Karel
        if (userRepository.findByUsername("Karel") == null) {
            User user = new User();
            user.setUsername("KarelGott");

            /*byte[] array = Files.readAllBytes(Paths.get(ResourceUtils.getFile("classpath:static/images/clenove/karel.jpg").getPath()));
            Image image = new Image(user.getUsername(), "image/jpeg", array);
            user.setImage(image);*/

            user.setFirstName("Karel");
            user.setLastName("Gott");
            user.setPassword(bCryptPasswordEncoder.encode("Karel"));
            user.setEmail("KarelGott@gmail.com");
            user.setRole(UserRole.SPECIALISTS);
            user.setArticleCount(13);
            user.setPostCount(55);
            user.setTopicCount(8);
            user.setEnabled(true);
            user.setAboutUs(true);
            user.setAboutMe("Jmenuji se Karel a laryngektomie byla pro m?? nezn??m??m pojmem, ne?? jsem se s n?? p??ed p??ti lety \\\"potkal\\\". Operac?? a l????bou si pro??la moje man??elka a j?? najednou vid??l celou problematiku zbl??zka - state??nost sv?? ??eny, um a ob??tavost zdravotn??k??. Vid??l jsem tak?? nedostatky, nap??. v n??zk?? informovanosti pacient?? i ve??ejnosti. Uv??tal jsem vznik spolku, sna????m se pom??hat a jako b??val?? novin???? i p??es m??dia. T?????? m?? vyd??v??n?? Zpravodaje a v??????m, ??e m?? na??e pr??ce smysl.");
            userRepository.save(user);
        }

        String[] typeOfArticle = new String[]{"setk??n??", "konference", "zpravodaj", "ozn??men??", "terapie", "sezen??"};

        for (String type : typeOfArticle) {
            //pridani typu clanku
            if (!articleTypeRepository.findByName(type).isPresent()) {
                ArticleType articleType = new ArticleType();
                articleType.setName(type);
                articleTypeRepository.save(articleType);
            }
        }


        //??lanky inicializace
        String zpravodajName = "Vyd??v??me zpravodaj";
        User articleAdmin = userRepository.findByUsername("admin");
        if (!articleService.findByName(zpravodajName).isPresent()) {
            Article article = new Article();
            article.setUser(articleAdmin);

           /* byte[] array = Files.readAllBytes(Paths.get(ResourceUtils.getFile("classpath:static/images/init/zpravodaj.jpg").getPath()));
            Image image = new Image("Zpravodaj", "image/jpeg", array);
            Collection<Image> images = new ArrayList<>();
            images.add(image);
            news.setImages(images);*/

            article.setArticleType(articleTypeRepository.findByName("zpravodaj").get());

            article.setName(zpravodajName);
            article.setText("<p><span style=\"color: rgb(85, 85, 85);\"><b>V posledn??m listopadov??m t??dnu vy??lo druh?? ????slo Zpravodaje a m??li byste ho ji?? m??t doma.</b></span><br></p><p style=\"\">z obsahu vyj??m??me:</p><ul><li>na co nebyl v ordinaci ??as:<br></li></ul><ul><li>odpov??di na ot??zky stran v????ivy</li></ul><ul><li>v??sledky ankety</li></ul><ul><li>Jak se n??m ??ije po laryngektomii:&nbsp;<span style=\"color: rgb(51, 51, 51);\">p????b??h pana Zde??ka</span></li></ul><ul><li>report???? z na??eho 2. setk??n?? v Pardubic??ch<br></li></ul><ul><li>podrobnosti o aplikaci z??chranka<br></li></ul><ul><li>a dal???? zaj??mavosti a rady</li></ul>");

            articleService.saveOrUpdate(article);
        }

        String setkani1 = "Dne 9. 1. 2020 prob??hlo pov??no??n?? t????kr??lov?? posezen??.";
        if (!articleService.findByName(setkani1).isPresent()) {
            Article article = new Article();
            article.setUser(articleAdmin);

            /*Collection<Image> images = new ArrayList<>();
            byte[] array = Files.readAllBytes(Paths.get(ResourceUtils.getFile("classpath:static/images/init/a.jpg").getPath()));
            byte[] array1 = Files.readAllBytes(Paths.get(ResourceUtils.getFile("classpath:static/images/init/b.jpg").getPath()));
            Image image = new Image("Setkani1", "image/jpeg", array);
            Image image1 = new Image("Setkana2", "image/jpeg", array1);
            images.add(image);
            images.add(image1);
            news.setImages(images);*/

            article.setArticleType(articleTypeRepository.findByName("terapie").get());

            article.setName(setkani1);
            article.setText("<p style=\"margin-left: 0px;\"><font>Tentokr??t jsme se se??li v komorn??j????m po??tu, v p????jemn??m prost??ed?? restaurace Fascila. Pozv??n?? na besedu p??ijal&nbsp;MUDr. Milan Vo??mik, Ph.D.,&nbsp;z??stupce p??ednosty Onkologick?? kliniky ve FN HK, p??edseda Sekce pro n??dory hlavy a krku v r??mci ??esk?? onkologick?? spole??nosti, kter?? n??m zodpov??d??l mnoho, pro n??s zaj??mav??ch, ot??zek.</font></p><p style=\"margin-left: 0px;\"><font>Klinick?? logopedka&nbsp;Mgr. Jarmila Hofmanov??&nbsp;n??s&nbsp;sezn??mila se sv??mi zku??enostmi s pou??it??m p????pravku <b>ENZYMEL</b>, s jeho ????inky, pou??it??m i s novou formou nejen gelu a zubn?? pasty, ale tak?? ??stn??ch pastilek.<br></font></p><p style=\"margin-left: 0px;\"><font>V??ichni ????astn??ci vyjad??ovali nad??en?? a uzn??n?? k vysok?? odbornosti na??ich host?? a k jejich laskav??mu p????stupu. Nejen, ??e jsme se dozv??d??li spoutu nov??ch informac??, tak?? jsme se zasm??li a odn????eli si mnoho t??mat k p??em????len??.<br></font></p><p style=\"margin-left: 0px;\"><font>V??ichni si v??????me ochoty a ??asu na??ich host??, kter?? n??m v??novali a je??t?? jednou jim&nbsp;D??KUJEME!</font></p>");

            articleService.saveOrUpdate(article);
        }

        String setkani2 = "Ve ??tvrtek 5. 12. 2019 jsme se se??li na spole??n?? logopedick?? terapii.";
        if (!articleService.findByName(setkani2).isPresent()) {
            Article article = new Article();
            article.setUser(articleAdmin);

            /*Collection<Image> images = new ArrayList<>();
            byte[] array = Files.readAllBytes(Paths.get(ResourceUtils.getFile("classpath:static/images/init/c.jpg").getPath()));
            byte[] array1 = Files.readAllBytes(Paths.get(ResourceUtils.getFile("classpath:static/images/init/d.jpg").getPath()));
            Image image = new Image("Setkani1", "image/jpeg", array);
            Image image1 = new Image("Setkana2", "image/jpeg", array1);
            images.add(image);
            images.add(image1);
            news.setImages(images);*/

            article.setArticleType(articleTypeRepository.findByName("setk??n??").get());

            article.setName(setkani2);
            article.setText("<p style=\"margin-left: 0px;\"><font>Ve ??tvrtek&nbsp;<font>5. 12. 2019&nbsp;</font>jsme se se??li na spole??n?? logopedick?? terapii. Krom?? toho, ??e to bylo velice p????jemn?? setk??n??, tak?? jsme pracovali i p??ed??vali sv?? zku??enosti.<br></font></p><p style=\"margin-left: 0px;\"><font>S velmi zaj??mav??mi \"vychyt??vkami\" ohledn?? kanyly a jej??ho kryt?? a dal????ho n??s seznamuje pan&nbsp;Jind??ich Habart,&nbsp;Jindro d??ky :)!</font></p><p style=\"margin-left: 0px;\"><font>Pod??kov??n?? pat???? t???? man??el??m&nbsp;L????ovi a Ivan?? Pla??kov??m&nbsp;- p??ivezli \"p??ebytky\" kanyl pro ods??v??n??. Tak pokud byste n??kdo pot??ebovali, jsou u m?? k dispozici</font>.</p>");
            articleService.saveOrUpdate(article);
        }

        fakeData();
        alreadySetup = true;
    }

    private void fakeData() {
        Faker faker = new Faker();
        List<Category> categories = categoryService.findAll();
        int catSize = categories.size();
        List<User> userList = userRepository.findAll();
        int userSize = userList.size();
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            topicService.saveOrUpdate(new Topic(faker.commerce().productName(), faker.commerce().productName(), userList.get(random.nextInt(userSize)), categories.get(random.nextInt(catSize))));
        }
        List<Topic> topicList = topicService.findAll();
        int topicSize = topicList.size();
        for (int i = 0; i < 1000; i++) {
            postService.saveOrUpdate(new Post(faker.book().title(), userList.get(random.nextInt(userSize)), topicList.get(random.nextInt(topicSize))));
        }
    }
}
