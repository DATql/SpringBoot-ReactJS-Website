import { useState } from "react";
import { GrFormNext } from "react-icons/gr";
import { Link } from "react-router-dom";
import { TableApplicationAdmin } from "./components";

const ApplicationsPageAdmin = () => {
  const [showBoxAdd, setShowBoxAdd] = useState(false);
  const [title, setTitle] = useState("");

  return (
    <section className="flex-grow">
      <div className="p-6 px-3 lg:px-6 block sm:flex items-center justify-between">
        <div className="mb-1 w-full">
          <div className="mb-4">
            <nav aria-label="Breadcrumb" className="text-sm font-semibold mb-6">
              <ol className="list-none p-0 inline-flex">
                <li className="flex items-center">
                  <Link
                    to="/admin"
                    className="text-gray-600 hover:text-purple-600"
                  >
                    Home
                  </Link>
                  <GrFormNext className="text-lg mx-2" />
                </li>
                <li className="flex items-center">
                  <Link
                    to="/admin/vips"
                    className="text-gray-600 hover:text-purple-600"
                  >
                    Applications
                  </Link>
                </li>
              </ol>
            </nav>
            <h1 className="text-xl sm:text-2xl font-semibold text-gray-700">
              Application management
            </h1>
          </div>
          <div className="block sm:flex items-center">
            <form className="sm:pr-3 mb-4 sm:mb-0">
              <label className="sr-only">Search</label>
              <div className="mt-1 relative sm:w-80 lg:w-96">
                <input
                  type="text"
                  name="category"
                  className="bg-gray-50 border border-gray-300 text-gray-700 text-sm rounded-lg block w-full p-2 focus:border-purple-700 focus:outline-none focus:ring-1 focus:ring-purple-700"
                  placeholder="Search for candidate..."
                  onChange={(e) => {
                    setTitle(e.target.value);
                  }}
                />
              </div>
            </form>
          </div>
        </div>
      </div>
      <TableApplicationAdmin name={title} />
    </section>
  );
};
export default ApplicationsPageAdmin;
