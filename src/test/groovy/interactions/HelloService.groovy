package interactions

/**
 * Class to user for Interaction Spec experiments
 */
class HelloService {
    int myInt

    String sayTypedHello(){'String hello'}

    def sayDefHello(){'def hello'}

    int sayIntHello(){1}

    Number sayNumberHello(){1}

    boolean helloActive(){true}

    def echo(Object obj){obj}
}
