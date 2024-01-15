export interface NavItemProps {
  href: string;
  text: string;
}
export function NavItem({ href, text }: NavItemProps) {
  return (
    <li className="nav-item">
      <a href={href} className="nav-link px-2 text-body-secondary">
        {text}
      </a>
    </li>
  );
}
