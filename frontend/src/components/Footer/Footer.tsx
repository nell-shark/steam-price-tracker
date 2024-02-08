import { NavItem, NavItemProps } from "./NavItem";

const navItems: NavItemProps[] = [
  { href: "#", text: "Home" },
  { href: "#", text: "Github" },
  { href: "#", text: "Login" }
];

export function Footer() {
  return (
    <footer className="my-5">
      <ul className="nav justify-content-center border-bottom pb-3 mb-3">
        {navItems.map(item => (
          <NavItem key={item.text} {...item} />
        ))}
      </ul>
      <p className="text-center text-body-secondary">Made with ❤️</p>
    </footer>
  );
}
