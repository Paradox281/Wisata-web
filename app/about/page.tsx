"use client"

import { useEffect } from "react"
import Link from "next/link"
import { motion } from "framer-motion"
import { Award, Users, Globe, Heart, MapPin, Mail, Phone } from "lucide-react"
import { Button } from "@/components/ui/button"

export default function AboutPage() {
  useEffect(() => {
    // Load AOS from CDN
    const loadAOS = async () => {
      if (typeof window !== "undefined") {
        // Load AOS CSS
        const link = document.createElement("link")
        link.rel = "stylesheet"
        link.href = "https://unpkg.com/aos@2.3.1/dist/aos.css"
        document.head.appendChild(link)

        // Load AOS JS
        const script = document.createElement("script")
        script.src = "https://unpkg.com/aos@2.3.1/dist/aos.js"
        script.onload = () => {
          if (window.AOS) {
            window.AOS.init({
              duration: 800,
              once: false,
            })
          }
        }
        document.head.appendChild(script)
      }
    }

    loadAOS()
  }, [])

  return (
    <main className="min-h-screen">
      {/* Hero Section */}
      <section className="relative h-[50vh] md:h-[60vh]">
        <img src="/placeholder.svg?height=800&width=1600" alt="About Altura" style={{ position: 'absolute', top: 0, left: 0, width: '100%', height: '100%', objectFit: 'cover' }} className="object-cover" />
        <div className="absolute inset-0 bg-black bg-opacity-50 flex items-center justify-center">
          <div className="text-center text-white px-4">
            <motion.h1
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.8 }}
              className="text-4xl md:text-5xl font-bold mb-4"
            >
              About Altura
            </motion.h1>
            <motion.p
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.8, delay: 0.2 }}
              className="text-xl max-w-3xl mx-auto"
            >
              Creating unforgettable travel experiences since 2010
            </motion.p>
          </div>
        </div>
      </section>

      {/* Our Story */}
      <section className="py-16 px-4 bg-white">
        <div className="max-w-7xl mx-auto">
          <div className="grid md:grid-cols-2 gap-12 items-center">
            <div data-aos="fade-right">
              <h2 className="text-3xl font-bold mb-6">Our Story</h2>
              <p className="text-gray-700 mb-4">
                Altura was founded in 2010 by a group of passionate travelers who believed that travel should be more
                than just visiting places – it should be about creating meaningful connections and unforgettable
                experiences.
              </p>
              <p className="text-gray-700 mb-4">
                What started as a small operation offering specialized tours in Southeast Asia has grown into a global
                travel company with a presence in over 50 countries. Despite our growth, we've remained true to our core
                values: authenticity, sustainability, and exceptional service.
              </p>
              <p className="text-gray-700">
                Our name "Altura" comes from the Spanish word for "height" or "elevation," reflecting our commitment to
                elevating the travel experience to new heights. We believe that travel has the power to transform lives,
                broaden perspectives, and create lasting memories.
              </p>
            </div>
            <div className="relative h-[400px] rounded-xl overflow-hidden" data-aos="fade-left">
              <img src="/placeholder.svg?height=800&width=600" alt="Altura Team" style={{ position: 'absolute', top: 0, left: 0, width: '100%', height: '100%', objectFit: 'cover' }} className="object-cover" />
            </div>
          </div>
        </div>
      </section>

      {/* Our Values */}
      <section className="py-16 px-4 bg-gray-50">
        <div className="max-w-7xl mx-auto">
          <h2 className="text-3xl font-bold mb-12 text-center" data-aos="fade-up">
            Our Values
          </h2>

          <div className="grid md:grid-cols-3 gap-8">
            <div className="bg-white p-6 rounded-xl shadow-sm" data-aos="fade-up" data-aos-delay="100">
              <div className="bg-blue-100 p-3 rounded-full w-fit mb-4">
                <Globe className="h-8 w-8 text-blue-600" />
              </div>
              <h3 className="text-xl font-bold mb-3">Sustainability</h3>
              <p className="text-gray-700">
                We are committed to responsible tourism that respects local cultures, supports local economies, and
                minimizes environmental impact. We partner with eco-friendly accommodations and support conservation
                efforts in the destinations we visit.
              </p>
            </div>

            <div className="bg-white p-6 rounded-xl shadow-sm" data-aos="fade-up" data-aos-delay="200">
              <div className="bg-blue-100 p-3 rounded-full w-fit mb-4">
                <Users className="h-8 w-8 text-blue-600" />
              </div>
              <h3 className="text-xl font-bold mb-3">Authenticity</h3>
              <p className="text-gray-700">
                We believe in creating genuine experiences that go beyond tourist attractions. Our tours include
                interactions with local communities, traditional cooking classes, and off-the-beaten-path adventures
                that showcase the true essence of each destination.
              </p>
            </div>

            <div className="bg-white p-6 rounded-xl shadow-sm" data-aos="fade-up" data-aos-delay="300">
              <div className="bg-blue-100 p-3 rounded-full w-fit mb-4">
                <Heart className="h-8 w-8 text-blue-600" />
              </div>
              <h3 className="text-xl font-bold mb-3">Excellence</h3>
              <p className="text-gray-700">
                From our knowledgeable guides to our carefully selected accommodations, we strive for excellence in
                every aspect of our service. We continuously seek feedback from our travelers to improve and refine our
                offerings.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* Our Team */}
      <section className="py-16 px-4 bg-white">
        <div className="max-w-7xl mx-auto">
          <h2 className="text-3xl font-bold mb-12 text-center" data-aos="fade-up">
            Meet Our Team
          </h2>

          <div className="grid md:grid-cols-4 gap-8">
            {teamMembers.map((member, index) => (
              <div key={index} className="text-center" data-aos="fade-up" data-aos-delay={index * 100}>
                <div className="relative h-64 rounded-xl overflow-hidden mb-4">
                  <img src={member.image || "/placeholder.svg"} alt={member.name} style={{ position: 'absolute', top: 0, left: 0, width: '100%', height: '100%', objectFit: 'cover' }} className="object-cover" />
                </div>
                <h3 className="text-xl font-bold">{member.name}</h3>
                <p className="text-blue-600 mb-2">{member.role}</p>
                <p className="text-gray-600 text-sm">{member.bio}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Achievements */}
      <section className="py-16 px-4 bg-gray-50">
        <div className="max-w-7xl mx-auto">
          <h2 className="text-3xl font-bold mb-12 text-center" data-aos="fade-up">
            Our Achievements
          </h2>

          <div className="grid md:grid-cols-4 gap-8">
            <div className="bg-white p-6 rounded-xl shadow-sm text-center" data-aos="fade-up" data-aos-delay="100">
              <div className="text-4xl font-bold text-blue-600 mb-2">50+</div>
              <p className="text-gray-700">Countries</p>
            </div>

            <div className="bg-white p-6 rounded-xl shadow-sm text-center" data-aos="fade-up" data-aos-delay="200">
              <div className="text-4xl font-bold text-blue-600 mb-2">10,000+</div>
              <p className="text-gray-700">Happy Travelers</p>
            </div>

            <div className="bg-white p-6 rounded-xl shadow-sm text-center" data-aos="fade-up" data-aos-delay="300">
              <div className="text-4xl font-bold text-blue-600 mb-2">200+</div>
              <p className="text-gray-700">Unique Tours</p>
            </div>

            <div className="bg-white p-6 rounded-xl shadow-sm text-center" data-aos="fade-up" data-aos-delay="400">
              <div className="text-4xl font-bold text-blue-600 mb-2">12</div>
              <p className="text-gray-700">Industry Awards</p>
            </div>
          </div>

          <div className="mt-12 text-center" data-aos="fade-up">
            <h3 className="text-xl font-bold mb-6 flex items-center justify-center">
              <Award className="h-6 w-6 text-yellow-500 mr-2" />
              Award-Winning Travel Company
            </h3>

            <div className="flex flex-wrap justify-center gap-4">
              <div className="bg-white p-4 rounded-lg shadow-sm">
                <p className="font-bold">Best Tour Operator 2023</p>
                <p className="text-sm text-gray-600">Travel Excellence Awards</p>
              </div>

              <div className="bg-white p-4 rounded-lg shadow-sm">
                <p className="font-bold">Sustainable Tourism Award</p>
                <p className="text-sm text-gray-600">Global Travel Initiative</p>
              </div>

              <div className="bg-white p-4 rounded-lg shadow-sm">
                <p className="font-bold">Customer Satisfaction</p>
                <p className="text-sm text-gray-600">Tourism Quality Board</p>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Contact Us */}
      <section className="py-16 px-4 bg-blue-600 text-white">
        <div className="max-w-7xl mx-auto">
          <div className="grid md:grid-cols-2 gap-12 items-center">
            <div data-aos="fade-right">
              <h2 className="text-3xl font-bold mb-6">Get In Touch</h2>
              <p className="mb-8">
                Have questions about our tours or want to create a custom itinerary? Our travel experts are here to help
                you plan the perfect trip.
              </p>

              <div className="space-y-4">
                <div className="flex items-center">
                  <MapPin className="h-5 w-5 mr-3" />
                  <span>123 Travel Street, Tourism City, 12345</span>
                </div>
                <div className="flex items-center">
                  <Phone className="h-5 w-5 mr-3" />
                  <span>+1 (555) 123-4567</span>
                </div>
                <div className="flex items-center">
                  <Mail className="h-5 w-5 mr-3" />
                  <span>info@altura-travel.com</span>
                </div>
              </div>
            </div>

            <div className="bg-white text-gray-900 p-8 rounded-xl shadow-lg" data-aos="fade-left">
              <h3 className="text-xl font-bold mb-6">Send us a message</h3>
              <form className="space-y-4">
                <div>
                  <label className="block text-sm font-medium mb-2">Name</label>
                  <input
                    type="text"
                    className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    placeholder="Your name"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium mb-2">Email</label>
                  <input
                    type="email"
                    className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    placeholder="Your email"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium mb-2">Message</label>
                  <textarea
                    rows={4}
                    className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                    placeholder="Tell us about your dream trip..."
                  ></textarea>
                </div>
                <Button className="w-full bg-blue-600 hover:bg-blue-700">Send Message</Button>
              </form>
            </div>
          </div>
        </div>
      </section>

      {/* Call to Action */}
      <section className="py-16 px-4 bg-white">
        <div className="max-w-4xl mx-auto text-center">
          <h2 className="text-3xl font-bold mb-6" data-aos="fade-up">
            Ready to Start Your Adventure?
          </h2>
          <p className="text-xl text-gray-600 mb-8" data-aos="fade-up" data-aos-delay="100">
            Join thousands of travelers who have discovered the world with Altura. Let us help you create memories that
            will last a lifetime.
          </p>
          <div className="flex flex-col sm:flex-row justify-center gap-4" data-aos="fade-up" data-aos-delay="200">
            <Button asChild size="lg">
              <Link href="/detail/featured">Explore Destinations</Link>
            </Button>
            <Button asChild variant="outline" size="lg">
              <Link href="#contact">Contact Us</Link>
            </Button>
          </div>
        </div>
      </section>
    </main>
  )
}

const teamMembers = [
  {
    name: "Elaina",
    role: "Founder & CEO",
    bio: "Passionate traveler with 15+ years in the tourism industry. Loves discovering hidden gems around the world.",
    image: "https://cdn.rafled.com/anime-icons/images/374yi72bsJLqPnyn3085StHiuZXNgKAc.jpg",
  },
  {
    name: "Alice Zuberg",
    role: "Head of Operations",
    bio: "Expert in logistics and tour planning. Ensures every trip runs smoothly from start to finish.",
    image: "https://i.redd.it/y77e61n0lwsa1.jpg",
  },
  {
    name: "Rui Tachibana",
    role: "Cultural Experience Director",
    bio: "Specializes in creating authentic cultural experiences and building relationships with local communities.",
    image: "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTOxcHlOLgBTlXqrMbqGKsE2Fzki2FXTE0lCg&s",
  },
  {
    name: "Rikka Takanashi",
    role: "Sustainability Manager",
    bio: "Dedicated to responsible tourism practices and environmental conservation initiatives.",
    image: "https://i.pinimg.com/564x/db/6e/77/db6e77106a10787b339da6e0b590410c.jpg",
  },
]
