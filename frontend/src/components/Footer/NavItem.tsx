import { Link } from "react-router-dom";

export interface NavItemProps {
  href: string;
  text: string;
}
export function NavItem({ href, text }: NavItemProps) {
  return (
    <li className="nav-item">
      <Link to={href} className="nav-link px-2 text-body-secondary">
        {text}
      </Link>
    </li>
  );
}
